package Software.PurchaseLedger;

import DbServer.DbManagerInterface;
import Software.Enums.*;
import Software.Utilities.Importer;
import Software.Inventory.DbManagerInventory;
import Software.Inventory.InventoryItem;
import org.apache.commons.csv.CSVRecord;

/**
 * Created by Michele on 08/11/2015.
 *
 *
 */
public class PurchaseLedgerImport extends Importer
{
    private DbManagerInterface dbManagerPurchaseLedger = new DbManagerPurchaseLedger();
    private DbManagerInventory dbManagerInventoryItems = new DbManagerInventory();

    private PurchaseLedgerLine purchaseLedgerLine;
    private InventoryItem inventoryItem;

    private String referenceChecker = "";


    private int invoiceNumber = new DbManagerPurchaseLedger().internalTransactionNumberGenerator();
    private int lineCounter = 1;
    private java.util.UUID invoiceUuid =  java.util.UUID.fromString(String.valueOf(new com.eaio.uuid.UUID()));

    private final int INVOICE_DATE = 0;
    private final int SUPPLIER_ID = 1;
    private final int EXTERNAL_INVOICE_REFERENCE = 2;
    private final int DESCRIPTION = 3;
    private final int PRODUCT_KEY = 4;
    private final int QUANTITY = 5;
    private final int PRICE = 6;
    private final int VAT = 7;
    private final int VAT_CODE = 8;
    private final int INVENTORY_RELEVANT = 9;
    private final int SHIPPED_FROM_COUNTRY = 10;
    private final int SHIPPED_TO_COUNTRY = 11;
    private final int SHIPPED_TO_CHANNEL = 12;
    private final int CURRENCY = 13;
    private final int COS_RELEVANT = 14;
    private final int ASSOCIATED_TRANSACTION_GROUP_KEY = 15;
    private final int ASSOCIATED_TRANSACTION_LINE_KEY = 16;
    private final int TRANSACTION_TYPE = 17;

    @Override
    public void completeImportProcess()
    {
        for (CSVRecord csvRecord : super.csvLines)
        {
            System.out.println(csvRecord.toString());
            // check that if current line is different from previous line
            String currentReference = csvRecord.get(INVOICE_DATE) +
                    csvRecord.get(SUPPLIER_ID)+csvRecord.get(EXTERNAL_INVOICE_REFERENCE);

            if(!referenceChecker.equals(currentReference))
            {
                // if different, a new invoice number is needed and line counter is reset to 1
                invoiceNumber = new DbManagerPurchaseLedger().internalTransactionNumberGenerator();
                lineCounter = 1;
                invoiceUuid = java.util.UUID.fromString(String.valueOf(new com.eaio.uuid.UUID()));
            }
            else
            {
                // if it is the same, only the lineCounter need to be increased
                lineCounter++;
            }

            // check if the transaction is increase in liability (eg. invoice)
            if(Double.parseDouble(csvRecord.get(PRICE))<0)
            {
                if (csvRecord.get(INVENTORY_RELEVANT).equals("YES"))
                {
                    importNegativeStockLine(csvRecord);
                }
                else if (csvRecord.get(COS_RELEVANT).equals("YES"))
                {
                    importPositiveCosLine(csvRecord,csvRecord.get(TRANSACTION_TYPE));
                }
                else
                {
                    importOverheadLine(csvRecord);
                }
                referenceChecker = currentReference;
            }
            // transaction is decrease in liability (eg. credit note)
            else
            {
                if (csvRecord.get(COS_RELEVANT).equals("YES"))
                {
                    importPositiveCosLine(csvRecord,csvRecord.get(TRANSACTION_TYPE));
                }
                else if(csvRecord.get(TRANSACTION_TYPE).equals(InventoryItemStatus.ADJUSTMENT_WRITE_OFF)) 
                {
                    importWriteOff(csvRecord);
                }
                else if(csvRecord.get(TRANSACTION_TYPE).equals(InventoryItemStatus.TRANSFER))
                {
                    importTransferInventoryItem(csvRecord);
                }   
                else
                {
                    importOverheadLine(csvRecord);
                }

            }
        }
    }

    private void importTransferInventoryItem(CSVRecord csvRecord)
    {
        for (int i = 1; i <= Integer.valueOf(csvRecord.get(QUANTITY)) ; i++)
        {

        }
    }

    private void importWriteOff(CSVRecord csvRecord)
    {
        for (int i = 1; i <= Integer.valueOf(csvRecord.get(QUANTITY)); i++)
        {
            dbManagerInventoryItems.adjustWriteOffItem(csvRecord.get(PRODUCT_KEY), csvRecord.get(SHIPPED_TO_CHANNEL),
                    csvRecord.get(INVOICE_DATE), Countries.valueOf(csvRecord.get(SHIPPED_TO_COUNTRY)));
        }
    }

    private void importNegativeStockLine(CSVRecord csvRecord)
    {
        purchaseLedgerLine = new PurchaseLedgerLine(csvRecord.get(INVOICE_DATE),
                csvRecord.get(SUPPLIER_ID), csvRecord.get(EXTERNAL_INVOICE_REFERENCE),
                csvRecord.get(DESCRIPTION), csvRecord.get(PRODUCT_KEY),
                Integer.valueOf(csvRecord.get(QUANTITY)), Double.valueOf(csvRecord.get(PRICE)),
                Double.valueOf(csvRecord.get(VAT)), csvRecord.get(VAT_CODE),
                Countries.valueOf(csvRecord.get(SHIPPED_FROM_COUNTRY)),
                Countries.valueOf(csvRecord.get(SHIPPED_TO_COUNTRY)),
                Channels.valueOf(csvRecord.get(SHIPPED_TO_CHANNEL)),
                invoiceNumber,lineCounter, invoiceUuid, Currencies.valueOf(csvRecord.get(CURRENCY)),
                PurchaseLedgerTransactionType.valueOf(csvRecord.get(TRANSACTION_TYPE)),true,
                false,null,null);

        // stock relevant invoices are associated with themselves
        purchaseLedgerLine.setProperty("associatedTransactionGroupReference",(invoiceNumber));
        purchaseLedgerLine.setProperty("associatedTransactionLineReference",(lineCounter));


        Double itemCost =  Double.valueOf(csvRecord.get(PRICE)) / Integer.valueOf(csvRecord.get(QUANTITY));

        // persist inventory items
        for (int i = 1; i <= Integer.valueOf(csvRecord.get(QUANTITY)); i++)
        {
            inventoryItem = new InventoryItem(invoiceNumber, lineCounter, i,
                    java.util.UUID.fromString(String.valueOf(purchaseLedgerLine.getProperty("invoiceUuid"))),
                    java.util.UUID.fromString(String.valueOf(purchaseLedgerLine.getProperty("transactionLineUUID"))),
                    java.util.UUID.fromString(String.valueOf(new com.eaio.uuid.UUID())),
                    purchaseLedgerLine.getProperty("productKey").toString(), itemCost,
                    Countries.valueOf(purchaseLedgerLine.getProperty("shippedToCountry").toString()),
                    Channels.valueOf(purchaseLedgerLine.getProperty("shippedToChannel").toString()),
                    Currencies.valueOf(purchaseLedgerLine.getProperty("currency").toString()),
                    purchaseLedgerLine.getProperty("date").toString());

            inventoryItem.setProperty("itemStatus",InventoryItemStatus.AVAILABLE_FOR_SALE.toString());
            inventoryItem.setProperty("currency",Currencies.valueOf(purchaseLedgerLine.getProperty("currency").toString()));

            dbManagerInventoryItems.persistTarget(inventoryItem);
        }
        dbManagerPurchaseLedger.persistTarget(purchaseLedgerLine);
    }

    private void importPositiveCosLine(CSVRecord csvRecord, String transactionType)
    {
        int transactionGroupKey = Integer.valueOf(csvRecord.get(ASSOCIATED_TRANSACTION_GROUP_KEY));
        int transactionLineKey = Integer.valueOf(csvRecord.get(ASSOCIATED_TRANSACTION_LINE_KEY));
        Countries countryFrom;
        Countries countryTo;
        Channels saleChannel;
        String productKey = "";

            // Check if there is an associated transaction line (on top of the transaction group key)
        if(transactionLineKey != 0)
        {
            countryFrom = Countries.valueOf(
                    new DbManagerPurchaseLedger().retrieveTransactionLineFromCountry(
                    transactionGroupKey, transactionLineKey));
            countryTo= Countries.valueOf(
                    new DbManagerPurchaseLedger().retrieveTransactionLineToCountry(
                            transactionGroupKey, transactionLineKey));
            saleChannel = Channels.valueOf(
                    new DbManagerPurchaseLedger().retrieveTransactionLineToChannel(
                            transactionGroupKey, transactionLineKey));
            productKey = new DbManagerPurchaseLedger().retrieveProductKey(
                    transactionGroupKey, transactionLineKey);
        }
        else // if associated transaction line is 0, the transaction is to split
                            // on the overall group of transactions (rather than the specific line)
        {
            countryFrom = Countries.valueOf(
                    new DbManagerPurchaseLedger().retrieveTransactionGroupFromCountry(
                            transactionGroupKey));
            countryTo= Countries.valueOf(
                    new DbManagerPurchaseLedger().retrieveTransactionGroupToCountry(
                            transactionGroupKey));
            saleChannel = Channels.valueOf(
                    new DbManagerPurchaseLedger().retrieveTransactionGroupToChannel(
                            transactionGroupKey));
        }

        System.out.println(countryFrom + " " + transactionGroupKey);

        // update inventory items COS section
        double cos = Double.parseDouble(csvRecord.get(PRICE));
        double cosPerUnit;
        int quantity;

        // update inventory items COS for overall batch purchase (eg. delivery cost)
        if(transactionLineKey == 0)
        {
            quantity = new DbManagerPurchaseLedger().retrieveTransactionGroupQuantity(transactionGroupKey);
            cosPerUnit = (cos / quantity);
            new DbManagerInventory().updateTransactionGroupCos(transactionGroupKey, cosPerUnit);
        }

        // update inventory items COS for specific items (eg. labelling cost only needed for a specific product)
        else
        {
            quantity = new DbManagerPurchaseLedger().retrieveTransactionLineQuantity(transactionGroupKey,
                    transactionLineKey);
            cosPerUnit = (cos / quantity);
            new DbManagerInventory().updateTransactionLineCos(transactionGroupKey, transactionLineKey,
                    cosPerUnit);
        }

        purchaseLedgerLine = new PurchaseLedgerLine(csvRecord.get(INVOICE_DATE),
                csvRecord.get(SUPPLIER_ID), csvRecord.get(EXTERNAL_INVOICE_REFERENCE),
                csvRecord.get(DESCRIPTION), productKey,
                Integer.valueOf(csvRecord.get(QUANTITY)),
                Double.valueOf(csvRecord.get(PRICE)), Double.valueOf(csvRecord.get(VAT)),
                csvRecord.get(VAT_CODE),null,null,null,
                invoiceNumber,lineCounter, invoiceUuid,
                Currencies.valueOf(csvRecord.get(CURRENCY)),
                PurchaseLedgerTransactionType.valueOf(transactionType),
                false,true, transactionGroupKey, transactionLineKey);

        purchaseLedgerLine.setProperty("shippedFromCountry",(countryFrom));
        purchaseLedgerLine.setProperty("shippedToCountry",(countryTo));
        purchaseLedgerLine.setProperty("shippedToChannel",(saleChannel));


        dbManagerPurchaseLedger.persistTarget(purchaseLedgerLine);

    }

    private void importOverheadLine(CSVRecord csvRecord)
    {
        int quantity = (csvRecord.get(QUANTITY).isEmpty() ? 1 : Integer.valueOf(csvRecord.get(QUANTITY)));
        
        purchaseLedgerLine = new PurchaseLedgerLine(csvRecord.get(INVOICE_DATE),
                csvRecord.get(SUPPLIER_ID), csvRecord.get(EXTERNAL_INVOICE_REFERENCE),
                csvRecord.get(DESCRIPTION), csvRecord.get(PRODUCT_KEY),
                quantity,
                Double.valueOf(csvRecord.get(PRICE)), Double.valueOf(csvRecord.get(VAT)),
                csvRecord.get(VAT_CODE),null,null,null,
                invoiceNumber,lineCounter, invoiceUuid,
                Currencies.valueOf(csvRecord.get(CURRENCY)),
                PurchaseLedgerTransactionType.valueOf(csvRecord.get(TRANSACTION_TYPE)),
                false,false,0,0);

        dbManagerPurchaseLedger.persistTarget(purchaseLedgerLine);
    }

}
