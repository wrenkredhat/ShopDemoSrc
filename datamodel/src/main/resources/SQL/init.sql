insert into MARKETPLACE ( id, ccy, marketid, shippingcosts, url, version ) values ( 1, 'USD', 'US', 10, 'localhost:8180', 1 );

insert into STOCK( ID ,MARKETID, PRODUCTID ,QTY, VERSION ) values ( 1,  '1', '1', 259, 1 );

insert into PRODUCT( ID ,NAME ,PRICE ,PRODUCTID ,VERSION ) values ( 1,'P1', 200, '1', 1  );

insert into FXRATE (ID ,BASECCY ,RATE ,TGTCCY ,VERSION ) values ( 1, 'USD', 2.5, 'USD', 1 );

commit;

