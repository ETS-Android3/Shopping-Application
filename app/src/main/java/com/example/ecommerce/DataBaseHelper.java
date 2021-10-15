package com.example.ecommerce;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static String databaseName = "final";
    SQLiteDatabase ECommerce;

    public DataBaseHelper(Context context)
    {
        super(context, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table customer (CustID integer primary key autoincrement, name text, username text UNIQUE, password text, gender text, birthdate text, job text);");
        db.execSQL("create table orderr (OrdID integer primary key autoincrement, date text, address text, submited integer DEFAULT 0, cust_id integer, FOREIGN KEY(cust_id) REFERENCES customer(CustID));");
        db.execSQL("create table category (CatID integer primary key autoincrement, catname text);");
        db.execSQL("create table product (ProID integer primary key autoincrement, proname text, price integer, quantity integer , cat_id integer, FOREIGN KEY(cat_id) REFERENCES category(CatID));");
        db.execSQL("create table order_details (ord_id integer, pro_id integer, quantity integer DEFAULT 1, FOREIGN KEY(ord_id) REFERENCES orderr(OrdID), FOREIGN KEY(pro_id) REFERENCES product(ProID));");
        addCategories(db);
        addProducts(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("drop table if exists customer");
        db.execSQL("drop table if exists orderr");
        db.execSQL("drop table if exists category");
        db.execSQL("drop table if exists product");
        db.execSQL("drop table if exists order_details");

        onCreate(db);
    }

    public void addCategories(SQLiteDatabase db)
    {
        ContentValues row = new ContentValues();
        row.put("catname", "Skincare");
        db.insert("category", null, row);

        row = new ContentValues();
        row.put("catname", "Haircare");
        db.insert("category", null, row);

        row = new ContentValues();
        row.put("catname", "Makeup");
        db.insert("category", null, row);

        row = new ContentValues();
        row.put("catname", "Perfume");
        db.insert("category", null, row);
    }

    public void addProducts(SQLiteDatabase db)
    {
        //Haircare cat
        ContentValues row = new ContentValues();
        row.put("proname", "Dermedic CAPILARATE SOOTHING SHAMPOO");
        row.put("price", "165");
        row.put("quantity", "20");
        row.put("cat_id", "2");
        db.insert("product", null, row);

        row = new ContentValues();
        row.put("proname", "AleoEva Argan Conditioner");
        row.put("price", "35");
        row.put("quantity", "40");
        row.put("cat_id", "2");
        db.insert("product", null, row);

        row = new ContentValues();
        row.put("proname", "Biopoint hairmask");
        row.put("price", "75");
        row.put("quantity", "50");
        row.put("cat_id", "2");
        db.insert("product", null, row);

        //Makeup cat
        row = new ContentValues();
        row.put("proname", "Huda Beauty Matte Power Bullet Lipstick - Rendezvous");
        row.put("price", "660");
        row.put("quantity", "25");
        row.put("cat_id", "3");
        db.insert("product", null, row);

        row = new ContentValues();
        row.put("proname", "Mac Studio Fix Fluid Spf 15 - Nc20");
        row.put("price", "1025");
        row.put("quantity", "12");
        row.put("cat_id", "3");
        db.insert("product", null, row);

        //Perfumes cat
        row = new ContentValues();
        row.put("proname", "Olympea Intense");
        row.put("price", "2020");
        row.put("quantity", "17");
        row.put("cat_id", "4");
        db.insert("product", null, row);

        //Skincare cat
        row = new ContentValues();
        row.put("proname", "Pharmaceris T facecream");
        row.put("price", "195");
        row.put("quantity", "10");
        row.put("cat_id", "1");
        db.insert("product", null, row);

        row = new ContentValues();
        row.put("proname", "Natavis Vitamin C serum");
        row.put("price", "465");
        row.put("quantity", "40");
        row.put("cat_id", "1");
        db.insert("product", null, row);

        row = new ContentValues();
        row.put("proname", "Avene Oily skin cleanser");
        row.put("price", "210");
        row.put("quantity", "30");
        row.put("cat_id", "1");
        db.insert("product", null, row);

        row = new ContentValues();
        row.put("proname", "Bionike sunblock spf  50");
        row.put("price", "445");
        row.put("quantity", "20");
        row.put("cat_id", "1");
        db.insert("product", null, row);
    }

    public void addCustomer(String name, String username, String password, String gender, String birthdate, String job)
    {
        ECommerce = getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put("name", name);
        row.put("username", username);
        row.put("password", password);
        row.put("gender", gender);
        row.put("birthdate", birthdate);
        row.put("job", job);

        ECommerce.insert("customer", null, row);
        ECommerce.close();
    }

    public void addOrder(String custID)
    {
        ECommerce = getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put("cust_id", custID);

        ECommerce.insert("orderr", null, row);
        ECommerce.close();
    }

    public void addOrderDetails(String ordid, String prodid)
    {
        ECommerce = getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put("ord_id", ordid);
        row.put("pro_id", prodid);

        ECommerce.insert("order_details", null, row);
        ECommerce.close();
    }

    public boolean CheckExsist(String username, String password)
    {
        ECommerce = this.getReadableDatabase();

        String[] arg = {username, password};
        Cursor c = ECommerce.rawQuery("select CustID from customer where username = ?   AND password = ? ", arg);
        // id=c.getInt(c.getColumnIndex("CustID"));
        return c.getCount() > 0;
    }

    public Boolean OrderExist(String cust)
    {
        ECommerce = getReadableDatabase();
        Cursor cursor = ECommerce.rawQuery("Select * from  orderr where cust_id = " + cust + " and submited = 0 ", null);
        if(cursor.getCount() <= 0){
            ECommerce.close();
            return false;
        }
        ECommerce.close();
        return true;
    }

    public Cursor fetchCustomerInfo(String username, String password)
    {
        ECommerce = getReadableDatabase();
        String[] rowDetails = {"CustID", "name", "username", "password", "gender", "birthdate", "job"};
        String [] arg = {username, password};

        Cursor cursor = ECommerce.query("customer", rowDetails, " username = ? AND password = ?", arg, null, null, null);
        if(cursor != null)
            cursor.moveToFirst();
        ECommerce.close();

        return cursor;
    }

    public Cursor fetchAllProducts()
    {
        ECommerce = getReadableDatabase();
        String[] rowDetails = {"ProID", "proname", "price", "quantity", "cat_id"};

        Cursor cursor = ECommerce.query("product", rowDetails, null, null, null, null, null);
        if(cursor != null)
            cursor.moveToFirst();
        ECommerce.close();

        return cursor;
    }

    public Cursor fetchCat()
    {
        ECommerce = getReadableDatabase();
        String[] rowDetails = {"CatID", "catname"};

        Cursor cursor = ECommerce.query("category", rowDetails, null, null, null, null, null);
        if(cursor != null)
            cursor.moveToFirst();
        ECommerce.close();

        return cursor;
    }

    public Cursor fecthCatProducts(String id)
    {
        ECommerce = getReadableDatabase();
        String[] rowDetails = {"ProID", "proname", "price", "quantity", "cat_id"};
        String [] arg = {id};

        Cursor cursor = ECommerce.query("product", rowDetails, "cat_id = ?", arg, null, null, null);
        if(cursor != null)
            cursor.moveToFirst();
        ECommerce.close();

        return cursor;
    }

    public Cursor fetchCart(String OrderID)
    {
        ECommerce = getReadableDatabase();
        String[] rowDetails = {"pro_id", "quantity"};
        String [] arg = {OrderID};

        Cursor cursor = ECommerce.query("order_details", rowDetails, "ord_id = ?", arg, null, null, null);
        if(cursor != null)
            cursor.moveToFirst();
        ECommerce.close();

        return cursor;
    }

    public Cursor fetchProductsInCart(String proID)
    {
        ECommerce = getReadableDatabase();
        String[] rowDetails = {"proname", "price", "cat_id"};
        String [] arg = {proID};

        Cursor cursor = ECommerce.query("product", rowDetails, "ProID = ?", arg, null, null, null);
        if(cursor != null)
            cursor.moveToFirst();
        ECommerce.close();

        return cursor;
    }

    public Cursor searchProducts(String value)
    {
        ECommerce = getReadableDatabase();
        String[] rowDetails = {"ProID", "proname", "price", "quantity", "cat_id"};
        String[] arg = {"%" + value + "%"};

        Cursor cursor = ECommerce.query("product", rowDetails, "proname like ?", arg, null, null, null);
        if(cursor != null)
            cursor.moveToFirst();
        ECommerce.close();

        return cursor;
    }

    public String getOrderID(String cust)
    {
        String id = null;
        ECommerce = getReadableDatabase();
        String[] rowDetails = {"OrdID"};
        String[] arg = {cust};

        Cursor cursor = ECommerce.query("orderr", rowDetails, "cust_id = ? and submited = 0", arg, null, null, null);
        if(cursor.getCount() <= 0)
            id = null;
        else {
            cursor.moveToNext();
            id = cursor.getString(0);
        }
        ECommerce.close();

        return id;
    }

    public String getProductID(String productName, String productprice, String productQ)
    {
        String id = null;
        ECommerce = getReadableDatabase();
        String[] rowDetails = {"ProID"};
        String[] arg = {productName, productprice, productQ};

        Cursor cursor = ECommerce.query("product", rowDetails, "proname = ? and price = ? and quantity = ?", arg, null, null, null);
        if(cursor.getCount() <= 0)
            id = null;
        else {
            cursor.moveToNext();
            id = cursor.getString(0);
        }
        ECommerce.close();
        return  id;
    }

    public String getProductForCart(String name, String p)
    {
        String id = null;
        ECommerce = getReadableDatabase();
        String[] rowDetails = {"ProID"};
        String[] arg = {name, p};

        Cursor cursor = ECommerce.query("product", rowDetails, "proname = ? and price = ?", arg, null, null, null);
        if(cursor.getCount() <= 0)
            id = null;
        else {
            cursor.moveToNext();
            id = cursor.getString(0);
        }
        ECommerce.close();
        return  id;
    }

    public void updateQuantity(String ordid, String prodid, String quan)
    {
        ECommerce = getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put("quantity", quan);
        String [] compare = {ordid, prodid};
        ECommerce.update("order_details", row, "ord_id = ? and pro_id = ?", compare);
        ECommerce.close();
    }

    public void updateOrder(String custID, String date, String address)
    {
        ECommerce = getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put("date", date);
        row.put("address", address);
        row.put("submited", 1);
        String[] compare = {custID};
        ECommerce.update("orderr", row, "cust_id = ? and submited = 0", compare);
        ECommerce.close();
    }

    public void updatePassword(String username, String pass)
    {
        ECommerce = getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put("password", pass);
        String[] compare = {username};
        ECommerce.update("customer", row, "username = ?", compare);
        ECommerce.close();
    }

    public void deleteProduct(String ordid, String prodid)
    {
        ECommerce = getWritableDatabase();
        String [] compare = {ordid, prodid};
        String[] args = {ordid, prodid};
        ECommerce.delete("order_details","ord_id = ? and pro_id = ? ", args);
        ECommerce.close();
    }
}
