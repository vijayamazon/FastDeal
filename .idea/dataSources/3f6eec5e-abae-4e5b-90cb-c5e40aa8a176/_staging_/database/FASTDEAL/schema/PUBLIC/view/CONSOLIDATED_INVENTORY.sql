CREATE FORCE VIEW PUBLIC.CONSOLIDATED_INVENTORY(INTERNAL_INVOICE_REFERENCE_KEY, INTERNAL_INVOICE_REFERENCE_LINE, ITEM_NUMBER, CONCATENATED_PRIMARY_KEY, INVOICE_UUID, INVOICE_LINE_UUID, PRODUCT_KEY, ITEM_COS, SINGLE_ITEM_UUID, ITEM_DATE, INVENTORY_ITEM_STATUS, INVENTORY_ITEM_CURRENCY, INVENTORY_ITEM_STORE_TIMESTAMP, INVENTORY_TRANSACTION_LOCKED, RETURN_COUNTER, TRANSACTION_TYPE, TRANSACTION_UUID) AS
  (SELECT
     INVENTORY_ITEMS_GB_AMAZON.INTERNAL_INVOICE_REFERENCE_KEY,
     INVENTORY_ITEMS_GB_AMAZON.INTERNAL_INVOICE_REFERENCE_LINE,
     INVENTORY_ITEMS_GB_AMAZON.ITEM_NUMBER,
     INVENTORY_ITEMS_GB_AMAZON.CONCATENATED_PRIMARY_KEY,
     INVENTORY_ITEMS_GB_AMAZON.INVOICE_UUID,
     INVENTORY_ITEMS_GB_AMAZON.INVOICE_LINE_UUID,
     INVENTORY_ITEMS_GB_AMAZON.PRODUCT_KEY,
     INVENTORY_ITEMS_GB_AMAZON.ITEM_COS,
     INVENTORY_ITEMS_GB_AMAZON.SINGLE_ITEM_UUID,
     INVENTORY_ITEMS_GB_AMAZON.ITEM_DATE,
     INVENTORY_ITEMS_GB_AMAZON.INVENTORY_ITEM_STATUS,
     INVENTORY_ITEMS_GB_AMAZON.INVENTORY_ITEM_CURRENCY,
     INVENTORY_ITEMS_GB_AMAZON.INVENTORY_ITEM_STORE_TIMESTAMP,
     INVENTORY_ITEMS_GB_AMAZON.INVENTORY_TRANSACTION_LOCKED,
     INVENTORY_ITEMS_GB_AMAZON.RETURN_COUNTER,
     INVENTORY_ITEMS_GB_AMAZON.TRANSACTION_TYPE,
     INVENTORY_ITEMS_GB_AMAZON.TRANSACTION_UUID
   FROM PUBLIC.INVENTORY_ITEMS_GB_AMAZON)
  UNION
  (SELECT
     INVENTORY_ITEMS_FR_AMAZON.INTERNAL_INVOICE_REFERENCE_KEY,
     INVENTORY_ITEMS_FR_AMAZON.INTERNAL_INVOICE_REFERENCE_LINE,
     INVENTORY_ITEMS_FR_AMAZON.ITEM_NUMBER,
     INVENTORY_ITEMS_FR_AMAZON.CONCATENATED_PRIMARY_KEY,
     INVENTORY_ITEMS_FR_AMAZON.INVOICE_UUID,
     INVENTORY_ITEMS_FR_AMAZON.INVOICE_LINE_UUID,
     INVENTORY_ITEMS_FR_AMAZON.PRODUCT_KEY,
     INVENTORY_ITEMS_FR_AMAZON.ITEM_COS,
     INVENTORY_ITEMS_FR_AMAZON.SINGLE_ITEM_UUID,
     INVENTORY_ITEMS_FR_AMAZON.ITEM_DATE,
     INVENTORY_ITEMS_FR_AMAZON.INVENTORY_ITEM_STATUS,
     INVENTORY_ITEMS_FR_AMAZON.INVENTORY_ITEM_CURRENCY,
     INVENTORY_ITEMS_FR_AMAZON.INVENTORY_ITEM_STORE_TIMESTAMP,
     INVENTORY_ITEMS_FR_AMAZON.INVENTORY_TRANSACTION_LOCKED,
     INVENTORY_ITEMS_FR_AMAZON.RETURN_COUNTER,
     INVENTORY_ITEMS_FR_AMAZON.TRANSACTION_TYPE,
     INVENTORY_ITEMS_FR_AMAZON.TRANSACTION_UUID
   FROM PUBLIC.INVENTORY_ITEMS_FR_AMAZON)
  UNION
  (SELECT
     INVENTORY_ITEMS_IT_AMAZON.INTERNAL_INVOICE_REFERENCE_KEY,
     INVENTORY_ITEMS_IT_AMAZON.INTERNAL_INVOICE_REFERENCE_LINE,
     INVENTORY_ITEMS_IT_AMAZON.ITEM_NUMBER,
     INVENTORY_ITEMS_IT_AMAZON.CONCATENATED_PRIMARY_KEY,
     INVENTORY_ITEMS_IT_AMAZON.INVOICE_UUID,
     INVENTORY_ITEMS_IT_AMAZON.INVOICE_LINE_UUID,
     INVENTORY_ITEMS_IT_AMAZON.PRODUCT_KEY,
     INVENTORY_ITEMS_IT_AMAZON.ITEM_COS,
     INVENTORY_ITEMS_IT_AMAZON.SINGLE_ITEM_UUID,
     INVENTORY_ITEMS_IT_AMAZON.ITEM_DATE,
     INVENTORY_ITEMS_IT_AMAZON.INVENTORY_ITEM_STATUS,
     INVENTORY_ITEMS_IT_AMAZON.INVENTORY_ITEM_CURRENCY,
     INVENTORY_ITEMS_IT_AMAZON.INVENTORY_ITEM_STORE_TIMESTAMP,
     INVENTORY_ITEMS_IT_AMAZON.INVENTORY_TRANSACTION_LOCKED,
     INVENTORY_ITEMS_IT_AMAZON.RETURN_COUNTER,
     INVENTORY_ITEMS_IT_AMAZON.TRANSACTION_TYPE,
     INVENTORY_ITEMS_IT_AMAZON.TRANSACTION_UUID
   FROM PUBLIC.INVENTORY_ITEMS_IT_AMAZON)