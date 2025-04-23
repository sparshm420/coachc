package com.example.cc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "CoachConnect.db";
    private static final int DATABASE_VERSION = 17;

    // Table Names
    public static final String TABLE_USER = "users";
    public static final String TABLE_ADMINS = "admins";
    public static final String TABLE_COACHES = "coaches";
    public static final String TABLE_LEARNERS = "learners";
    public static final String TABLE_BOOKING = "bookings";
    public static final String TABLE_CLASS = "classes";

    // Common Columns
    public static final String USER_ID = "id";
    public static final String USER_NAME = "name";
    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWORD = "password";
    public static final String USER_PREFERENCES = "preferences";
    public static final String USER_PHONE = "phone";
    public static final String USER_AVATAR = "avatar";

    // Booking Columns
    public static final String BOOKING_ID = "id";
    public static final String BOOKING_CLASS_NAME = "class_name";
    public static final String BOOKING_CLASS_TIME = "class_time";
    public static final String BOOKING_CLASS_CATEGORY = "class_category";
    public static final String BOOKING_USER_ID = "user_id";

    // Class Columns
    public static final String CLASS_ID = "id";
    public static final String CLASS_NAME = "name";
    public static final String CLASS_CATEGORY = "category";
    public static final String CLASS_TIME = "time";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        db.execSQL("DROP TABLE IF EXISTS reviews");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMINS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACHES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEARNERS);

        db.execSQL("CREATE TABLE " + TABLE_USER + " (" +
                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_NAME + " TEXT, " +
                USER_EMAIL + " TEXT UNIQUE, " +
                USER_PASSWORD + " TEXT, " +
                USER_PREFERENCES + " TEXT, " +
                USER_PHONE + " TEXT, " +
                USER_AVATAR + " BLOB)");

        db.execSQL("CREATE TABLE " + TABLE_BOOKING + " (" +
                BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BOOKING_CLASS_NAME + " TEXT, " +
                BOOKING_CLASS_TIME + " TEXT, " +
                BOOKING_CLASS_CATEGORY + " TEXT, " +
                BOOKING_USER_ID + " INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_CLASS + " (" +
                CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CLASS_NAME + " TEXT, " +
                CLASS_CATEGORY + " TEXT, " +
                CLASS_TIME + " TEXT, " +
                "latitude REAL, " +
                "longitude REAL, " +
                "created_by TEXT)");

        db.execSQL("CREATE TABLE reviews (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "class_name TEXT, " +
                "rating INTEGER, " +
                "comment TEXT)");

        db.execSQL("CREATE TABLE attendance (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "learner_id INTEGER, " +
                "coach_email TEXT, " +
                "class_name TEXT, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");


        db.execSQL("CREATE TABLE " + TABLE_ADMINS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT UNIQUE, password TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_COACHES + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT UNIQUE, password TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_LEARNERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT UNIQUE, password TEXT)");

        // ✅ UPDATED sample inserts with lat, lon, created_by
        db.execSQL("INSERT INTO " + TABLE_CLASS + " (name, category, time, latitude, longitude, created_by) VALUES ('Zenith Yoga Studio', 'Yoga', '08:00 AM', -6.7862, 39.2323, 'coach1@example.com')");
        db.execSQL("INSERT INTO " + TABLE_CLASS + " (name, category, time, latitude, longitude, created_by) VALUES ('Aqua Splash Pool', 'Swimming', '10:30 AM', -6.7701, 39.2468, 'coach2@example.com')");
        db.execSQL("INSERT INTO " + TABLE_CLASS + " (name, category, time, latitude, longitude, created_by) VALUES ('FitKick Football Arena', 'Football', '05:00 PM', -6.8013, 39.2795, 'coach3@example.com')");
        db.execSQL("INSERT INTO " + TABLE_CLASS + " (name, category, time, latitude, longitude, created_by) VALUES ('Cricket Pro Academy', 'Cricket', '03:00 PM', -6.7541, 39.2712, 'coach4@example.com')");
        db.execSQL("INSERT INTO " + TABLE_CLASS + " (name, category, time, latitude, longitude, created_by) VALUES ('Coastal Zen Yoga', 'Yoga', '6:00 AM', 13.3530, 74.7865, 'coach1@example.com')");
        db.execSQL("INSERT INTO " + TABLE_CLASS + " (name, category, time, latitude, longitude, created_by) VALUES ('Udupi Swim Champs', 'Swimming', '10:00 AM', 13.3542, 74.7859, 'coach2@example.com')");
        db.execSQL("INSERT INTO " + TABLE_CLASS + " (name, category, time, latitude, longitude, created_by) VALUES ('FitFlex Football Ground', 'Football', '5:00 PM', 13.3515, 74.7882, 'coach3@example.com')");
        db.execSQL("INSERT INTO " + TABLE_CLASS + " (name, category, time, latitude, longitude, created_by) VALUES ('Beachside HIIT Blasters', 'Fitness', '7:30 AM', 13.3508, 74.7871, 'coach4@example.com')");
        db.execSQL("INSERT INTO " + TABLE_CLASS + " (name, category, time, latitude, longitude, created_by) VALUES ('Shree Cricket Academy', 'Cricket', '3:00 PM', 13.3552, 74.7899, 'coach1@example.com')");
        db.execSQL("INSERT INTO classes (name, category, time, latitude, longitude, created_by) " +
                "VALUES ('Googleplex Yoga Zone', 'Yoga', '08:00 AM', 37.4229, -122.0841, 'coach@nearby.com')");

        db.execSQL("INSERT INTO classes (name, category, time, latitude, longitude, created_by) " +
                "VALUES ('Bay Area Swim Champs', 'Swimming', '10:30 AM', 37.4218, -122.0835, 'coach@nearby.com')");

        db.execSQL("INSERT INTO classes (name, category, time, latitude, longitude, created_by) " +
                "VALUES ('Mountain View HIIT', 'Cricket', '03:00 PM', 37.4215, -122.0829, 'coach@nearby.com')");

        db.execSQL("INSERT INTO classes (name, category, time, latitude, longitude, created_by) " +
                "VALUES ('Silicon Valley Fitness', 'Football', '05:00 PM', 37.4230, -122.0845, 'coach@nearby.com')");

        db.execSQL("INSERT INTO classes (name, category, time, latitude, longitude, created_by) " +
                "VALUES ('Android Studio Bootcamp', 'Yoga', '06:00 PM', 37.4223, -122.0830, 'coach@nearby.com')");

        db.execSQL("INSERT INTO admins (name, email, password) VALUES " +
                "('Admin One', 'admin1@example.com', 'adminpass1')," +
                "('Admin Two', 'admin2@example.com', 'adminpass2');");
        db.execSQL("INSERT INTO coaches (name, email, password) VALUES " +
                "('Coach Alice', 'coach1@example.com', 'coachpass1')," +
                "('Coach Bob', 'coach2@example.com', 'coachpass2')," +
                "('Coach Charlie', 'coach3@example.com', 'coachpass3');");
        db.execSQL("INSERT INTO learners (name, email, password) VALUES " +
                "('Learner John', 'learner1@example.com', 'learnpass1')," +
                "('Learner Maya', 'learner2@example.com', 'learnpass2')," +
                "('Learner Zane', 'learner3@example.com', 'learnpass3');");








        db.execSQL("CREATE TABLE queries (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "message TEXT, " +
                "response TEXT, " +
                "is_read INTEGER DEFAULT 0)");




    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS reviews");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMINS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACHES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEARNERS);
        onCreate(db);
    }

    public boolean registerUser(String name, String email, String password, String preferences, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, name);
        values.put(USER_EMAIL, email);
        values.put(USER_PASSWORD, password);
        values.put(USER_PREFERENCES, preferences);
        values.put(USER_PHONE, phone);
        return db.insert(TABLE_USER, null, values) != -1;
    }

    public boolean registerAdmin(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        return db.insert(TABLE_ADMINS, null, values) != -1;
    }



    // Insert query
    public boolean insertQuery(int userId, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("message", message);
        return db.insert("queries", null, values) != -1;
    }

    // Get user queries
    public Cursor getUserQueries(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM queries WHERE user_id = ?", new String[]{String.valueOf(userId)});
    }
    public Cursor getAttendanceByClassId(int classId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT learner_name, date FROM attendance WHERE class_id = ?", new String[]{String.valueOf(classId)});
    }


    // Check for unread responses
    public boolean hasUnreadResponses(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM queries WHERE user_id = ? AND response IS NOT NULL AND is_read = 0",
                new String[]{String.valueOf(userId)});
        boolean hasUnread = cursor.getCount() > 0;
        cursor.close();
        return hasUnread;
    }

    public Cursor getAttendanceByLearner(int learnerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT class_name, coach_email, timestamp FROM attendance WHERE learner_id = ?",
                new String[]{String.valueOf(learnerId)});
    }


    // Mark all queries as read
    public void markQueriesAsRead(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_read", 1);
        db.update("queries", values, "user_id = ? AND response IS NOT NULL", new String[]{String.valueOf(userId)});
    }



    public boolean registerCoach(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        return db.insert(TABLE_COACHES, null, values) != -1;
    }

    public boolean registerLearner(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        return db.insert(TABLE_LEARNERS, null, values) != -1;
    }

    public Cursor getAllPendingQueries() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM queries ORDER BY id DESC", null);
    }

    public boolean respondToQuery(int queryId, String response) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("response", response);
        values.put("is_read", 0);  // Mark as unread for user
        return db.update("queries", values, "id = ?", new String[]{String.valueOf(queryId)}) > 0;
    }


    public Cursor validateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USER +
                " WHERE " + USER_EMAIL + " = ? AND " + USER_PASSWORD + " = ?", new String[]{email, password});
    }

    public Cursor validateFromTable(String email, String password, String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + tableName + " WHERE email = ? AND password = ?", new String[]{email, password});
    }

    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + USER_EMAIL + " = ?", new String[]{email});
    }

    public boolean updateUserProfile(int userId, String name, String email, String phone, String avatarUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, name);
        values.put(USER_EMAIL, email);
        values.put(USER_PHONE, phone);
        values.put(USER_AVATAR, avatarUri);
        return db.update(TABLE_USER, values, USER_ID + " = ?", new String[]{String.valueOf(userId)}) > 0;
    }

    public boolean addBooking(int userId, String className, String classTime, String classCategory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BOOKING_USER_ID, userId);
        values.put(BOOKING_CLASS_NAME, className);
        values.put(BOOKING_CLASS_TIME, classTime);
        values.put(BOOKING_CLASS_CATEGORY, classCategory);
        return db.insert(TABLE_BOOKING, null, values) != -1;
    }

    public int getTableCount(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public String getTopRatedClassName() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT class_name, AVG(rating) as avg_rating FROM reviews GROUP BY class_name ORDER BY avg_rating DESC LIMIT 1", null
        );

        String topClass = null;
        if (cursor.moveToFirst()) {
            topClass = cursor.getString(cursor.getColumnIndexOrThrow("class_name"));
        }
        cursor.close();
        return topClass;
    }

    public String getMostActiveCoach() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT created_by, COUNT(*) as total FROM classes GROUP BY created_by ORDER BY total DESC LIMIT 1", null
        );

        String coachEmail = null;
        if (cursor.moveToFirst()) {
            coachEmail = cursor.getString(cursor.getColumnIndexOrThrow("created_by"));
        }
        cursor.close();
        return coachEmail;
    }




    public Cursor getBookingsByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BOOKING + " WHERE " + BOOKING_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    public boolean deleteBooking(int bookingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BOOKING, BOOKING_ID + " = ?", new String[]{String.valueOf(bookingId)}) > 0;
    }

    public String getTopLearnerEmail() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT user_id, COUNT(*) as count FROM bookings GROUP BY user_id ORDER BY count DESC LIMIT 1", null
        );
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
            Cursor userCursor = db.rawQuery("SELECT email FROM learners WHERE id = ?", new String[]{String.valueOf(userId)});
            String email = userCursor.moveToFirst() ? userCursor.getString(userCursor.getColumnIndexOrThrow("email")) : "-";
            userCursor.close();
            cursor.close();
            return email;
        }
        cursor.close();
        return "-";
    }

    public String getPeakBookingTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT class_time, COUNT(*) as count FROM bookings GROUP BY class_time ORDER BY count DESC LIMIT 1", null
        );
        String time = cursor.moveToFirst() ? cursor.getString(cursor.getColumnIndexOrThrow("class_time")) : "-";
        cursor.close();
        return time;
    }

    public String getTopLearner() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT learners.name, COUNT(bookings.id) AS booking_count " +
                "FROM learners " +
                "JOIN bookings ON learners.id = bookings.user_id " +
                "GROUP BY learners.name " +
                "ORDER BY booking_count DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            cursor.close();
            return name;
        }
        cursor.close();
        return null;
    }

    public String getMostPopularTimeSlot() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT class_time, COUNT(*) as count FROM bookings GROUP BY class_time ORDER BY count DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            String time = cursor.getString(0);
            cursor.close();
            return time;
        }
        cursor.close();
        return null;
    }



    public String getMostBookedClass() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT class_name, COUNT(*) as count FROM bookings GROUP BY class_name ORDER BY count DESC LIMIT 1", null
        );
        String topBooked = cursor.moveToFirst() ? cursor.getString(cursor.getColumnIndexOrThrow("class_name")) : "-";
        cursor.close();
        return topBooked;
    }




    public boolean insertClass(String name, String category, String time, double lat, double lon, String createdBy) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLASS_NAME, name);
        values.put(CLASS_CATEGORY, category);
        values.put(CLASS_TIME, time);
        values.put("latitude", lat);
        values.put("longitude", lon);
        values.put("created_by", createdBy);
        return db.insert(TABLE_CLASS, null, values) != -1;
    }


    public List<String> getAllClasses(String category) {
        List<String> classList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = (category == null || category.isEmpty())
                ? db.rawQuery("SELECT * FROM " + TABLE_CLASS, null)
                : db.rawQuery("SELECT * FROM " + TABLE_CLASS + " WHERE " + CLASS_CATEGORY + " = ?", new String[]{category});

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_NAME));
                String cat = cursor.getString(cursor.getColumnIndexOrThrow(CLASS_CATEGORY));
                classList.add(name + " (" + cat + ")");
            } while (cursor.moveToNext());
        }

        cursor.close();
        return classList;
    }

    public Cursor getClassByName(String className) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CLASS + " WHERE " + CLASS_NAME + " = ?", new String[]{className});
    }

    public boolean insertReview(int userId, String className, int rating, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("class_name", className);
        values.put("rating", rating);
        values.put("comment", comment);
        return db.insert("reviews", null, values) != -1;
    }

    public Cursor getAttendanceByLearnerId(int learnerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM attendance WHERE learner_id = ?", new String[]{String.valueOf(learnerId)});
    }

    public Cursor getAttendanceByLearnerAndClass(int learnerId, String className) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT timestamp FROM attendance WHERE learner_id = ? AND class_name = ?",
                new String[]{String.valueOf(learnerId), className});
    }

    public boolean hasUserReviewedClass(int userId, String className) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM reviews WHERE user_id = ? AND class_name = ?", new String[]{String.valueOf(userId), className});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public float getAverageRating(String className) {
        SQLiteDatabase db = this.getReadableDatabase();
        float avg = 0;
        Cursor cursor = db.rawQuery("SELECT AVG(rating) as avg_rating FROM reviews WHERE class_name = ?", new String[]{className});
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            avg = cursor.getFloat(cursor.getColumnIndexOrThrow("avg_rating"));
        }
        cursor.close();
        return avg;
    }

    public List<String> getReviewsForClass(String className) {
        List<String> reviews = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT rating, comment FROM reviews WHERE class_name = ?", new String[]{className});
        while (cursor.moveToNext()) {
            int rating = cursor.getInt(cursor.getColumnIndexOrThrow("rating"));
            String comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"));
            reviews.add("★" + rating + (comment != null && !comment.isEmpty() ? " - " + comment : ""));
        }
        cursor.close();
        return reviews;
    }

    public List<String> getTopRatedClasses(int limit) {
        List<String> topClasses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT class_name, AVG(rating) as avg_rating FROM reviews GROUP BY class_name ORDER BY avg_rating DESC LIMIT ?", new String[]{String.valueOf(limit)});
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("class_name"));
            float rating = cursor.getFloat(cursor.getColumnIndexOrThrow("avg_rating"));
            Cursor classCursor = db.rawQuery("SELECT category FROM classes WHERE name = ?", new String[]{name});
            String category = classCursor.moveToFirst() ? classCursor.getString(classCursor.getColumnIndexOrThrow("category")) : "Unknown";
            classCursor.close();
            topClasses.add(name + " (" + category + ") ★" + String.format("%.1f", rating));
        }
        cursor.close();
        return topClasses;
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USER, null);
    }

    // Insert class with creator's email
    public boolean insertClass(String name, String category, String time, String createdBy) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("category", category);
        values.put("time", time);
        values.put("created_by", createdBy);
        return db.insert("classes", null, values) != -1;
    }

    // Get all classes (for learner & admin)
    public Cursor getAllClasses() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM classes", null);
    }

    // Delete class by ID
    public boolean deleteClass(int classId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Get class name before deleting (to clean up bookings)
        Cursor cursor = db.rawQuery("SELECT name FROM " + TABLE_CLASS + " WHERE id = ?", new String[]{String.valueOf(classId)});
        String classNameToDelete = null;

        if (cursor != null && cursor.moveToFirst()) {
            classNameToDelete = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            cursor.close();
        }

        // Delete class
        int deletedRows = db.delete(TABLE_CLASS, "id = ?", new String[]{String.valueOf(classId)});

        // Delete related bookings by class name (optional: also check time if needed)
        if (classNameToDelete != null) {
            db.delete(TABLE_BOOKING, BOOKING_CLASS_NAME + " = ?", new String[]{classNameToDelete});
        }

        return deletedRows > 0;
    }




    public Cursor getClassesByCoach(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CLASS + " WHERE created_by = ?", new String[]{email});
    }

    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USER, USER_ID + " = ?", new String[]{String.valueOf(userId)}) > 0;
    }

    public int getCountFromTable(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public Cursor getAllFromTable(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + tableName, null);
    }

    public Cursor getBookingsByClassName(String className) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BOOKING + " WHERE " + BOOKING_CLASS_NAME + " = ?", new String[]{className});
    }

    public Cursor getLearnerById(int learnerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_LEARNERS + " WHERE id = ?", new String[]{String.valueOf(learnerId)});
    }


    public boolean deleteUserFromTable(int userId, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(tableName, "id = ?", new String[]{String.valueOf(userId)}) > 0;
    }

    public Cursor getLearnerByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM learners WHERE email = ?", new String[]{email});
    }

    public Cursor getCoachByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM coaches WHERE email = ?", new String[]{email});
    }

    public Cursor getAdminByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM admins WHERE email = ?", new String[]{email});
    }


    public Cursor getAllBookings() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BOOKING, null);
    }

    public boolean markAttendance(int learnerId, String coachEmail, String className) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("learner_id", learnerId);
        values.put("coach_email", coachEmail);
        values.put("class_name", className);
        return db.insert("attendance", null, values) != -1;
    }

    public Cursor getAttendanceByCoach(String coachEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM attendance WHERE coach_email = ?", new String[]{coachEmail});
    }

    public Cursor getBookingsByUserAndClass(int userId, String className) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM bookings WHERE user_id = ? AND class_name = ?", new String[]{
                String.valueOf(userId), className
        });
    }



}
