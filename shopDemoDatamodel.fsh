
echo "Creating DataModel-Project";


clear;

#m -rf datamodel;

project-new --named datamodel --topLevelPackage org.trader.demo --type jar --projectFolder datamodel


jpa-setup --provider Hibernate --container JBOSS_AS7;


jpa-new-entity --named Marketplace --targetPackage org.trader.demo.model.jpa;
jpa-new-field --named MarketID;
jpa-new-field --named CCY;
jpa-new-field --named URL;
jpa-new-field --named ShippingCosts --type long;
cd ..;

jpa-new-entity --named Product --targetPackage org.trader.demo.model.jpa;
jpa-new-field  --named ProductID;
jpa-new-field  --named Name;
cd ..;

jpa-new-entity --named Stock --targetPackage org.trader.demo.model.jpa;
jpa-new-field  --named MarketID;
jpa-new-field  --named ProductID;
jpa-new-field  --named Qty --type int;
cd ..;


jpa-new-entity --named ShopOrder --targetPackage org.trader.demo.model.jpa;
jpa-new-field  --named UserEmail;
jpa-new-field  --named MarketIdSrc;
jpa-new-field  --named MarketIdTgt;
jpa-new-field  --named ProductID;
jpa-new-field  --named ShippingCosts --type int;
jpa-new-field  --named TotalPrice --type int;
jpa-new-field  --named Qty --type int;
jpa-new-field  --named Rate --type double;
cd ..;

jpa-new-entity --named FXRate --targetPackage org.trader.demo.model.jpa;
jpa-new-field  --named BaseCCY;
jpa-new-field  --named TgtCCY;
jpa-new-field  --named Rate --type double;
cd ..;

# Generate bare POJOS -----------------------------------------------------;

#java-new-class --named Marketplace --targetPackage org.trader.demo.model.pojo;
#java-new-field --named MarketID;
#java-new-field --named CCY;
#java-new-field --named URL;
#java-new-field --named ShippingCosts --type long;
#cd ..;
#
#java-new-class --named Product --targetPackage org.trader.demo.model.pojo;
#java-new-field  --named ProductID;
#java-new-field  --named Name;
#cd ..;
#
#java-new-class --named Stock --targetPackage org.trader.demo.model.pojo;
#java-new-field  --named MarketID;
#java-new-field  --named ProductID;
#java-new-field  --named Qty --type int;
#cd ..;
#
#
#java-new-class --named Order --targetPackage org.trader.demo.model.pojo;
#java-new-field  --named User;
#java-new-field  --named MarketIdSrc;
#java-new-field  --named MarketIdTgt;
#java-new-field  --named ProductID;
#java-new-field  --named Qty --type int;
#java-new-field  --named Rate --type double;
#cd ..;
#
#java-new-class --named FXRate --targetPackage org.trader.demo.model.pojo;
#java-new-field  --named BaseCCY;
#java-new-field  --named TgtCCY;
#java-new-field  --named Rate --type double;
#cd ..;

rest-generate-endpoints-from-entities --org.trader.demo.model.jpa
cd ~~;
build;






