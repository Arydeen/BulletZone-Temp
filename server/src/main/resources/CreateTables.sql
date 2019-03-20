BEGIN NOT ATOMIC
    # Common table -- lists global unrelated constants in a single row
    CREATE TABLE IF NOT EXISTS Common
    (
      Version VARCHAR(40) NOT NULL
    );

    # Status table -- enumeration of statuses for items/accounts
    #                 that can be made inactive or deleted
    CREATE TABLE IF NOT EXISTS Status
    (
      StatusID INT NOT NULL,
      Name VARCHAR(40) NOT NULL,
      PRIMARY KEY (StatusID)
    );

    # User table -- basic information for user accounts
    CREATE TABLE IF NOT EXISTS User
    (
      UserID INT NOT NULL AUTO_INCREMENT,
      Name VARCHAR(40) NOT NULL,
      StatusID INT NOT NULL,
      PRIMARY KEY (UserID),
      FOREIGN KEY (StatusID) REFERENCES Status(StatusID)
    );

    # Permission table -- enumeration of access permissions that users can have on items/accounts
    CREATE TABLE IF NOT EXISTS Permission
    (
      PermissionID INT NOT NULL,
      Name VARCHAR(40) NOT NULL,
      PRIMARY KEY (PermissionID)
    );

    #CREATE TABLE IF NOT EXISTS Location/Map
    #(
    #);

    # ItemPropertyType table -- enumeration of the way in which a given property
    #                           combines when two items of the same type share a property
    CREATE TABLE IF NOT EXISTS ItemPropertyType
    (
      ItemPropertyTypeID INT NOT NULL,
      Name VARCHAR(40) NOT NULL,
      PRIMARY KEY (ItemPropertyTypeID)
    );

    # BankAccount table -- vital information for bank accounts in the game
    CREATE TABLE IF NOT EXISTS BankAccount
    (
      BankAccountID INT NOT NULL AUTO_INCREMENT,
      Credits FLOAT NOT NULL,
      StatusID INT NOT NULL,
      PRIMARY KEY (BankAccountID),
      FOREIGN KEY (StatusID) REFERENCES Status(StatusID)
    );

    # BankAccount_User_Permissions table -- correlation showing which users have which
    #                                       permissions on a given bank account
    CREATE TABLE IF NOT EXISTS BankAccount_User_Permissions
    (
      BankAccountID INT NOT NULL,
      UserID INT NOT NULL,
      PermissionID INT NOT NULL,
      FOREIGN KEY (BankAccountID) REFERENCES BankAccount(BankAccountID),
      FOREIGN KEY (UserID) REFERENCES User(UserID),
      FOREIGN KEY (PermissionID) REFERENCES Permission(PermissionID)
    );

    # ItemProperty table -- enumeration of properties that some game
    #                       items can have but that most don't have
    CREATE TABLE IF NOT EXISTS ItemProperty
    (
      ItemPropertyID INT NOT NULL,
      Name VARCHAR(40) NOT NULL,
      ItemPropertyTypeID INT NOT NULL,
      PRIMARY KEY (ItemPropertyID),
      FOREIGN KEY (ItemPropertyTypeID) REFERENCES ItemPropertyType(ItemPropertyTypeID)
    );

    # ItemCategory table -- enumeration of major classifications for item typess
    CREATE TABLE IF NOT EXISTS ItemCategory
    (
      ItemCategoryID INT NOT NULL,
      Name VARCHAR(40) NOT NULL,
      ItemPropertyID1 INT NOT NULL,
      ItemPropertyID2 INT NOT NULL,
      ItemPropertyID3 INT NOT NULL,
      PRIMARY KEY (ItemCategoryID),
      FOREIGN KEY (ItemPropertyID1) REFERENCES ItemProperty(ItemPropertyID),
      FOREIGN KEY (ItemPropertyID2) REFERENCES ItemProperty(ItemPropertyID),
      FOREIGN KEY (ItemPropertyID3) REFERENCES ItemProperty(ItemPropertyID)
    );

    # ItemType table -- vital information on individual item types
    CREATE TABLE ItemType
    (
      ItemTypeID INT NOT NULL,
      Name VARCHAR(40) NOT NULL,
      ItemCategoryID INT NOT NULL,
      Size FLOAT NOT NULL,
      Weight FLOAT NOT NULL,
      Price FLOAT NOT NULL,
      PropertyVal1 FLOAT NOT NULL,
      PropertyVal2 FLOAT NOT NULL,
      PropertyVal3 FLOAT NOT NULL,
      PRIMARY KEY (ItemTypeID),
      FOREIGN KEY (ItemCategoryID) REFERENCES ItemCategory(ItemCategoryID)
    );

    # Item table -- vital information on individual items
    CREATE TABLE Item
    (
      ItemID INT NOT NULL AUTO_INCREMENT,
      ItemTypeID INT NOT NULL,
      UsageMonitor FLOAT NOT NULL,
      StatusID INT NOT NULL,
      PRIMARY KEY (ItemID),
      FOREIGN KEY (ItemTypeID) REFERENCES ItemType(ItemTypeID),
      FOREIGN KEY (StatusID) REFERENCES Status(StatusID)
    );

    # Item container -- additional information for containers beyond what is in the item table
    CREATE TABLE IF NOT EXISTS ItemContainer
    (
      ItemID INT NOT NULL, -- must have matching ItemID in Item table to be valid
      Name VARCHAR(40) NOT NULL,
      PRIMARY KEY (ItemID),
      FOREIGN KEY (ItemID) REFERENCES Item(ItemID)
    );

    # ItemContainer_Item -- correlation indicating which items are in which containers,
    #                       with an indication of how the item is situated in the container
    CREATE TABLE IF NOT EXISTS ItemContainer_Item
    (
      ItemID INT NOT NULL,
      Container_ItemID INT NOT NULL,
      StartSlot INT NOT NULL,   -- first slot in the container where the item appears
      EndSlot INT NOT NULL,     -- last slot in the container where the item appears
      Modifier INT NOT NULL,    -- such as orientation of item in the slots
      FOREIGN KEY (ItemID) REFERENCES Item(ItemID),
      FOREIGN KEY (Container_ItemID) REFERENCES ItemContainer(ItemID)
    );

    # ItemContainer_User_Permissions table -- correlation showing which users have which
    #                                         permissions on a given container
    CREATE TABLE IF NOT EXISTS ItemContainer_User_Permissions
    (
      ItemID INT NOT NULL,  -- should be an ItemContainer ItemID only, not individual items
      UserID INT NOT NULL,
      PermissionID INT NOT NULL,
      FOREIGN KEY (ItemID) REFERENCES ItemContainer(ItemID),
      FOREIGN KEY (UserID) REFERENCES User(UserID),
      FOREIGN KEY (PermissionID) REFERENCES Permission(PermissionID)
    );

END