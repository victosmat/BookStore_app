package com.example.BookStore_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.BookStore_app.model.Bill;
import com.example.BookStore_app.model.BillDetails;
import com.example.BookStore_app.model.Book;
import com.example.BookStore_app.model.Cart;
import com.example.BookStore_app.model.CartBook;
import com.example.BookStore_app.model.Statistical;
import com.example.BookStore_app.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BookStore.db";
    private static int DATABASE_VERSION = 1;
    private String monthYear;


    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE user(" + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "name TEXT, dateOfBirth TEXT, gender TEXT, position TEXT, username TEXT, password TEXT)";
        db.execSQL(sql);

        sql = "CREATE TABLE book(" + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "name TEXT, author TEXT, category TEXT, price TEXT, urlImage TEXT)";
        db.execSQL(sql);

        sql = "CREATE TABLE cart(" + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "userId INTEGER," + "foreign key(userId) references user(id))";
        db.execSQL(sql);

        sql = "CREATE TABLE cartBook(" + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "bookId INTEGER, cartId INTEGER, quantity INTEGER, isChecked INTEGER," + "foreign key(bookId) references book(id)," + "foreign key(cartId) references cart(id))";
        db.execSQL(sql);

        sql = "CREATE TABLE bill(" + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "date TEXT, total TEXT, userId INTEGER," + "foreign key(userId) references user(id))";
        db.execSQL(sql);

        sql = "CREATE TABLE billDetails(" + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "billId INTEGER, bookId TEXT, quantity INTEGER," + "foreign key(billId) references bill(id))";
        db.execSQL(sql);

        // TẠO Statistical
        sql = "CREATE TABLE statistical(" + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "total INTEGER, bookId INTEGER," + "foreign key(bookId) references book(id))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    // xử lý user

    public List<User> getAllUser() {
        List<User> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("user", null, null, null, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            int id = rs.getInt(0);
            String name = rs.getString(1);
            String dateOfBirth = rs.getString(2);
            String gender = rs.getString(3);
            String position = rs.getString(4);
            String username = rs.getString(5);
            String password = rs.getString(6);
            list.add(new User(id, name, dateOfBirth, gender, position, username, password));
        }
        return list;
    }

    public User getUserByUsername(String username) {
        User user = new User();
        String whereClause = "username = ?";
        String[] whereArgs = {String.valueOf(username)};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("user", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            user.setId(rs.getInt(0));
            user.setName(rs.getString(1));
            user.setDateOfBirth(rs.getString(2));
            user.setGender(rs.getString(3));
            user.setPosition(rs.getString(4));
            user.setUsername(rs.getString(5));
            user.setPassword(rs.getString(6));
        }
        return user;
    }

    public long addUser(User user) {
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("dateOfBirth", user.getDateOfBirth());
        values.put("gender", user.getGender());
        values.put("position", user.getPosition());
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());

        // kiểm tra xem username đã tồn tại hay chưa
        String whereClause = "username like ?";
        String[] whereArgs = {"%" + user.getUsername() + "%"};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("user", null, whereClause, whereArgs, null, null, null);
        if (rs.getCount() > 0) {
            return -1;
        } else {
            SQLiteDatabase st = getWritableDatabase();
            return st.insert("user", null, values);
        }
    }

    public Long addCart(int userId) {
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        SQLiteDatabase st = getWritableDatabase();
        return st.insert("cart", null, values);
    }

    public Boolean checkBookInCart(int bookId, int cartId) {
        String whereClause = "bookId = ? and cartId = ?";
        String[] whereArgs = {String.valueOf(bookId), String.valueOf(cartId)};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("cartBook", null, whereClause, whereArgs, null, null, null);
        if (rs.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Long addCartBook(int bookId, int cartId, int quantity) {
        ContentValues values = new ContentValues();
        values.put("bookId", bookId);
        values.put("cartId", cartId);
        values.put("quantity", quantity);
        values.put("isChecked", 0);
        SQLiteDatabase st = getWritableDatabase();
        return st.insert("cartBook", null, values);
    }

    // update isChecked cartBook
    public int updateIsCheckedCartBook(int cartBookId, int isChecked) {
        ContentValues values = new ContentValues();
        values.put("isChecked", isChecked);
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(cartBookId)};
        SQLiteDatabase st = getWritableDatabase();
        return st.update("cartBook", values, whereClause, whereArgs);
    }

    public Long addBill(String date, String total, int userId) {
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("total", total);
        values.put("userId", userId);
        SQLiteDatabase st = getWritableDatabase();
        return st.insert("bill", null, values);
    }


    public Long addBillDetails(int billId, int bookId, int quantity) {
        ContentValues values = new ContentValues();
        values.put("billId", billId);
        values.put("bookId", bookId);
        values.put("quantity", quantity);
        SQLiteDatabase st = getWritableDatabase();
        int billDetailsId = (int) st.insert("billDetails", null, values);

        // update quantity statistical
        Statistical statistical = getStatisticalByBookId(bookId);
        int total = statistical.getTotal() + quantity;
        values = new ContentValues();
        values.put("total", total);
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(statistical.getId())};
        st = getWritableDatabase();
        return Long.valueOf(st.update("statistical", values, whereClause, whereArgs));
    }

    private Statistical getStatisticalByBookId(int bookId) {
        Statistical statistical = new Statistical();
        String whereClause = "bookId = ?";
        String[] whereArgs = {String.valueOf(bookId)};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("statistical", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            statistical.setId(rs.getInt(0));
            statistical.setBook(getBookById(rs.getInt(2)));
            statistical.setTotal(rs.getInt(1));
        }
        return statistical;
    }

    public List<CartBook> getAllCartBookByUserId(int userId) {
        List<CartBook> list = new ArrayList<>();
        int cartId = getCartById(userId).getId();
        String whereClause = "cartId = ?";
        String[] whereArgs = {String.valueOf(cartId)};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("cartBook", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            int id = rs.getInt(0);
            int bookId = rs.getInt(1);
            int quantity = rs.getInt(3);
            int isChecked = rs.getInt(4);
            list.add(new CartBook(id, getBookById(bookId), getCartById(userId), quantity, isChecked));
        }
        return list;
    }

    public List<Bill> getAllBillByUserId(int userId) {
        List<Bill> list = new ArrayList<>();
        String whereClause = "userId = ?";
        String[] whereArgs = {String.valueOf(userId)};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("bill", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            int id = rs.getInt(0);
            String date = rs.getString(1);
            String total = rs.getString(2);
            list.add(new Bill(id, date, total, getCartById(userId).getUser()));
        }
        return list;
    }

    public List<BillDetails> getAllBillDetailsByBillId(int billId) {
        List<BillDetails> list = new ArrayList<>();
        String whereClause = "billId = ?";
        String[] whereArgs = {String.valueOf(billId)};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("billDetails", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            int id = rs.getInt(0);
            int bookId = rs.getInt(2);
            int quantity = rs.getInt(3);
            list.add(new BillDetails(id, getBillById(billId), getBookById(bookId), quantity));
        }
        return list;
    }

    public User getUserById(int id) {
        User user = new User();
        String whereClause = "id = ?";
        String[] whereArgs = {Integer.toString(id)};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("user", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            user.setId(rs.getInt(0));
            user.setName(rs.getString(1));
            user.setDateOfBirth(rs.getString(2));
            user.setGender(rs.getString(3));
            user.setPosition(rs.getString(4));
            user.setUsername(rs.getString(5));
            user.setPassword(rs.getString(6));
        }
        return user;
    }

    public Cart getCartById(int id) {
        Cart cart = new Cart();
        String whereClause = "userId = ?";
        String[] whereArgs = {Integer.toString(id)};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("cart", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            cart.setId(rs.getInt(0));
            cart.setUser(getUserById(rs.getInt(1)));
        }
        return cart;
    }

    public Book getBookById(int id) {
        Book book = new Book();
        String whereClause = "id = ?";
        String[] whereArgs = {Integer.toString(id)};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("book", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            book.setId(rs.getInt(0));
            book.setName(rs.getString(1));
            book.setAuthor(rs.getString(2));
            book.setCategory(rs.getString(3));
            book.setPrice(rs.getString(4));
            book.setUrlImage(rs.getString(5));
        }
        return book;
    }

    public int deleteUser(int id) {
        String whereClause = "id = ?";
        String[] whereArgs = {Integer.toString(id)};
        SQLiteDatabase st = getWritableDatabase();
        return st.delete("user", whereClause, whereArgs);
    }

    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("dateOfBirth", user.getDateOfBirth());
        values.put("gender", user.getGender());
        values.put("position", user.getPosition());
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        String whereClause = "id = ?";
        String[] whereArgs = new String[]{Integer.toString(user.getId())};
        SQLiteDatabase st = getWritableDatabase();
        return st.update("user", values, whereClause, whereArgs);
    }

    // tìm kiếm user theo tên
    public List<User> searchByName(String name) {
        List<User> list = new ArrayList<>();
        String whereClause = "name like ?";
        String[] whereArgs = {"%" + name + "%"};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("user", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            int id = rs.getInt(0);
            String dateOfBirth = rs.getString(2);
            String gender = rs.getString(3);
            String position = rs.getString(4);
            String username = rs.getString(5);
            String password = rs.getString(6);
            list.add(new User(id, getUserById(id).getName(), dateOfBirth, gender, position, username, password));
        }
        return list;
    }

    // xử lý book

    public List<Book> getAllBook() {
        List<Book> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("book", null, null, null, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            int id = rs.getInt(0);
            String name = rs.getString(1);
            String author = rs.getString(2);
            String category = rs.getString(3);
            String price = rs.getString(4);
            String urlImage = rs.getString(5);
            list.add(new Book(id, name, author, category, price, urlImage));
        }
        return list;
    }

    // add book
    public long addBook(Book book) {
        ContentValues values = new ContentValues();
        values.put("name", book.getName());
        values.put("author", book.getAuthor());
        values.put("category", book.getCategory());
        values.put("price", book.getPrice());
        SQLiteDatabase st = getWritableDatabase();
        int bookId = (int) st.insert("book", null, values);

        values = new ContentValues();
        values.put("total", 0);
        values.put("bookId", bookId);
        st = getWritableDatabase();
        return st.insert("statistical", null, values);
    }

    public int updateBook(Book book) {
        ContentValues values = new ContentValues();
        values.put("name", book.getName());
        values.put("author", book.getAuthor());
        values.put("category", book.getCategory());
        values.put("price", book.getPrice());
        String whereClause = "id = ?";
        String[] whereArgs = new String[]{Integer.toString(book.getId())};
        SQLiteDatabase st = getWritableDatabase();
        return st.update("book", values, whereClause, whereArgs);
    }

    public int deleteBook(int id) {
        String whereClause = "id = ?";
        String[] whereArgs = {Integer.toString(id)};
        SQLiteDatabase st = getWritableDatabase();
        return st.delete("book", whereClause, whereArgs);
    }

    public List<Book> searchByNameBook(String name) {
        List<Book> list = new ArrayList<>();
        String whereClause = "name like ?";
        String[] whereArgs = {"%" + name + "%"};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("book", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            int id = rs.getInt(0);
            String author = rs.getString(2);
            String category = rs.getString(3);
            String price = rs.getString(4);
            String urlImage = rs.getString(5);
            list.add(new Book(id, getBookById(id).getName(), author, category, price, urlImage));
        }
        return list;
    }

    public List<CartBook> searchByNameBookInCart(Cart cart, String name) {
        List<Book> books = new ArrayList<>();
        String whereClause = "cartId = ?";
        String[] whereArgs = {Integer.toString(cart.getId())};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("cartBook", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            int bookId = rs.getInt(1);
            Book book = getBookById(bookId);
            if (book.getName().contains(name)) {
                books.add(book);
            }
        }
        List<CartBook> cartBooks = new ArrayList<>();
        whereClause = "cartId = ? bookId in (";
        for (int i = 0; i < books.size(); i++) {
            if (i == books.size() - 1) whereClause += books.get(i).getId() + ")";
            else whereClause += books.get(i).getId() + ",";
        }
        whereArgs = new String[]{Integer.toString(cart.getId())};
        rs = sqLiteDatabase.query("cartBook", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            int cartBookId = rs.getInt(0);
            int bookId = rs.getInt(1);
            int quantity = rs.getInt(3);
            int isChecked = rs.getInt(4);
            Book book = getBookById(bookId);
            cartBooks.add(new CartBook(cartBookId, book, cart, quantity, isChecked));
        }
        return cartBooks;
    }

    public List<Book> searchByCategoryBook(String category) {
        List<Book> list = new ArrayList<>();
        String whereClause = "category like ?";
        String[] whereArgs = {"%" + category + "%"};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("book", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            int id = rs.getInt(0);
            String name = rs.getString(1);
            String author = rs.getString(2);
            String price = rs.getString(4);
            String urlImage = rs.getString(5);
            list.add(new Book(id, name, author, category, price, urlImage));
        }
        return list;
    }

    public Cart getCartByUserId(int id) {
        Cart cart = new Cart();
        String whereClause = "userId = ?";
        String[] whereArgs = {Integer.toString(id)};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("cart", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            int cartId = rs.getInt(0);
            int userId = rs.getInt(1);
            cart.setId(cartId);
            cart.setUser(getUserById(userId));
        }
        return cart;
    }

    public void updateQuantityCartBook(int id, int number) {
        ContentValues values = new ContentValues();
        values.put("quantity", number);
        String whereClause = "id = ?";
        String[] whereArgs = new String[]{Integer.toString(id)};
        SQLiteDatabase st = getWritableDatabase();
        st.update("cartBook", values, whereClause, whereArgs);
    }

    public void deleteCartBook(int id) {
        String whereClause = "id = ?";
        String[] whereArgs = {Integer.toString(id)};
        SQLiteDatabase st = getWritableDatabase();
        st.delete("cartBook", whereClause, whereArgs);
    }

    public Bill getBillById(int id) {
        Bill bill = new Bill();
        String whereClause = "id = ?";
        String[] whereArgs = {Integer.toString(id)};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("bill", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            bill.setId(rs.getInt(0));
            bill.setDate(rs.getString(1));
            bill.setTotal(rs.getString(2));
            bill.setUser(getUserById(rs.getInt(3)));
        }
        return bill;
    }

    public List<Bill> getAllBillByUserIdAndDate(int id, String from, String to) {
        List<Bill> list = new ArrayList<>();
        String whereClause = "userId = ? and date between ? and ?";
        String[] whereArgs = {Integer.toString(id), from, to};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("bill", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            int billId = rs.getInt(0);
            String date = rs.getString(1);
            String total = rs.getString(2);
            User user = getUserById(rs.getInt(3));
            list.add(new Bill(billId, date, total, user));
        }
        return list;
    }

    public List<Statistical> getAllStatistical() {
        List<Statistical> statisticals = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String order = "total DESC";
        Cursor rs = sqLiteDatabase.query("statistical", null, null, null, null, null, order);
        while ((rs != null) && (rs.moveToNext())) {
            int id = rs.getInt(0);
            int total = rs.getInt(1);
            Book book = getBookById(rs.getInt(2));
            statisticals.add(new Statistical(id, book, total));
        }
        return statisticals;
    }

    public List<Statistical> getAllStatisticalByMonth(String month, String year) {
        String yearMonth = "";
        if (month.length() == 1) yearMonth += year + "/" + "0" + month;
        else yearMonth += year + "/" + month;
        List<Bill> bills = new ArrayList<>();
        String whereClause = "date like ?";
        String[] whereArgs = {yearMonth + "/%"};
        Log.e("month", yearMonth + "/%");
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("bill", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            int billId = rs.getInt(0);
            String date = rs.getString(1);
            String total = rs.getString(2);
            User user = getUserById(rs.getInt(3));
            bills.add(new Bill(billId, date, total, user));
        }

        List<Statistical> statisticals = new ArrayList<>();
        for (Bill bill : bills) {
            List<BillDetails> billBooks = getAllBillDetailsByBillId(bill.getId());
            for (BillDetails billBook : billBooks) {
                Book book = billBook.getBook();
                int quantity = billBook.getQuantity();
                // kiểm tra xem sách đã có trong list chưa
                boolean isExist = false;
                for (Statistical statistical : statisticals) {
                    if (statistical.getBook().getId() == book.getId()) {
                        statistical.setTotal(statistical.getTotal() + quantity);
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    statisticals.add(new Statistical(book, quantity));
                }
            }
        }
        // sort by total DESC
        Collections.sort(statisticals, new Comparator<Statistical>() {
            @Override
            public int compare(Statistical o1, Statistical o2) {
                return o2.getTotal() - o1.getTotal();
            }
        });
        return statisticals;
    }

    public void updateBookImage(int bookId, String url) {
        ContentValues values = new ContentValues();
        values.put("urlImage", url);
        String whereClause = "id = ?";
        String[] whereArgs = new String[]{Integer.toString(bookId)};
        SQLiteDatabase st = getWritableDatabase();
        st.update("book", values, whereClause, whereArgs);
    }

    public List<Statistical> getAllStatisticalByDate(String date, String month, String year) {
        String yearMonthDate = "";
        if (month.length() == 1) yearMonthDate += year + "/" + "0" + month;
        else yearMonthDate += year + "/" + month;
        if (date.length() == 1) yearMonthDate += "/" + "0" + date;
        else yearMonthDate += "/" + date;
        List<Bill> bills = new ArrayList<>();
        String whereClause = "date like ?";
        String[] whereArgs = {yearMonthDate + "%"};
        Log.e("month", yearMonthDate + "%");
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("bill", null, whereClause, whereArgs, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            int billId = rs.getInt(0);
            String date1 = rs.getString(1);
            String total = rs.getString(2);
            User user = getUserById(rs.getInt(3));
            bills.add(new Bill(billId, date1, total, user));
        }

        List<Statistical> statisticals = new ArrayList<>();
        for (Bill bill : bills) {
            List<BillDetails> billBooks = getAllBillDetailsByBillId(bill.getId());
            for (BillDetails billBook : billBooks) {
                Book book = billBook.getBook();
                int quantity = billBook.getQuantity();
                // kiểm tra xem sách đã có trong list chưa
                boolean isExist = false;
                for (Statistical statistical : statisticals) {
                    if (statistical.getBook().getId() == book.getId()) {
                        statistical.setTotal(statistical.getTotal() + quantity);
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    statisticals.add(new Statistical(book, quantity));
                }
            }
        }
        // sort by total DESC
        Collections.sort(statisticals, new Comparator<Statistical>() {
            @Override
            public int compare(Statistical o1, Statistical o2) {
                return o2.getTotal() - o1.getTotal();
            }
        });
        return statisticals;
    }

    public List<String> getAllEmail() {
        List<String> emails = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("user", null, null, null, null, null, null);
        while ((rs != null) && (rs.moveToNext())) {
            String email = rs.getString(4);
            emails.add(email);
        }
        return emails;
    }

    public void deleteBookInBillDetails(int id) {
        String whereClause = "bookId = ?";
        String[] whereArgs = new String[]{Integer.toString(id)};
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete("billDetails", whereClause, whereArgs);
    }

    public void deleteStatistical(int id) {
        String whereClause = "bookId = ?";
        String[] whereArgs = new String[]{Integer.toString(id)};
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete("statistical", whereClause, whereArgs);
    }

    public void deleteBookInCartBook(int id) {
        String whereClause = "bookId = ?";
        String[] whereArgs = new String[]{Integer.toString(id)};
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete("cartBook", whereClause, whereArgs);
    }

    public void deleteBill(int id) {
        String whereClause = "id = ?";
        String[] whereArgs = new String[]{Integer.toString(id)};
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete("bill", whereClause, whereArgs);
    }
}
