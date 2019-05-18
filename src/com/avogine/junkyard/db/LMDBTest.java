package com.avogine.junkyard.db;

import java.io.File;
import java.nio.IntBuffer;
import java.util.Objects;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.lmdb.LMDB;
import org.lwjgl.util.lmdb.MDBVal;

public class LMDBTest {

	public static void main(String[] args) {
		File dir = createDatabaseDirectory("lmdb");

		long env;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			PointerBuffer pp = stack.mallocPointer(1);
			E(LMDB.mdb_env_create(pp));
			env = pp.get(0);
		}

		try {
			// Open environment
			E(LMDB.mdb_env_open(env, dir.getPath(), 0, 0664));

			// Open database
			int dbi = openDatabase(env);

			// Put a value
			put(env, dbi, 1, "LWJGL");
			if (!"LWJGL".equals(get(env, dbi, 1))) {
				throw new IllegalStateException();
			} else {
				System.out.println("Found some LWJGL");
			}

			// Put a value, zero copy encoding
			putZeroCopy(env, dbi, 2, "LMDB");
			if (!"LMDB".equals(get(env, dbi, 2))) {
				throw new IllegalStateException();
			} else {
				System.out.println("Found some LMDB");
			}
		} finally {
			// Close environment
			LMDB.mdb_env_close(env);
		}
	}

	private static void put(long env, int dbi, int key, String value) {
		transaction(env, (stack, txn) -> {
			MDBVal kv = MDBVal.callocStack(stack)
					.mv_data(stack.malloc(4).putInt(0, key));

			// value is encoded to the MDBVal struct
			MDBVal dv = MDBVal.callocStack(stack)
					.mv_data(stack.UTF8(value, false));

			// the encoded text is copied to the database
			E(LMDB.mdb_put(txn, dbi, kv, dv, 0));

			return null;
		});
	}

	private static void putZeroCopy(long env, int dbi, int key, String value) {
		transaction(env, (stack, txn) -> {
			MDBVal kv = MDBVal.callocStack(stack)
					.mv_data(stack.malloc(4).putInt(0, key));

			// request enough bytes for the UTF8 encoded value
			MDBVal dv = MDBVal.callocStack(stack)
					.mv_size(MemoryUtil.memLengthUTF8(value, false));

			// no copy, LMDB updates dv.mv_data with a pointer to the database
			E(LMDB.mdb_put(txn, dbi, kv, dv, LMDB.MDB_RESERVE));
			// value is encoded directly to the memory-mapped file
			MemoryUtil.memUTF8(value, false, Objects.requireNonNull(dv.mv_data()));

			return null;
		});
	}

	private static String get(long env, int dbi, int key) {
		return transaction(env, (stack, txn) -> {
			MDBVal kv = MDBVal.callocStack(stack)
					.mv_data(stack.malloc(4).putInt(0, key));

			MDBVal dv = MDBVal.callocStack(stack);

			E(LMDB.mdb_get(txn, dbi, kv, dv));
			return MemoryUtil.memUTF8(Objects.requireNonNull(dv.mv_data()));
		});
	}

	static File createDatabaseDirectory(String directory) {
		File dir = new File(directory);

		dir.mkdir();
		dir.deleteOnExit();

		new File(dir, "data.mdb").deleteOnExit();
		new File(dir, "lock.mdb").deleteOnExit();

		return dir;
	}

	static void deleteDatabaseDirectory(String directory) {
		File dir = new File(directory);

		new File(dir, "data.mdb").delete();
		new File(dir, "lock.mdb").delete();

		dir.delete();
	}

	static void E(int rc) {
		if (rc != LMDB.MDB_SUCCESS) {
			throw new IllegalStateException(LMDB.mdb_strerror(rc));
		}
	}

	@FunctionalInterface
	interface Transaction<T> {
		T exec(MemoryStack stack, long txn);
	}

	static <T> T transaction(long env, Transaction<T> transaction) {
		T ret;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			PointerBuffer pp = stack.mallocPointer(1);

			E(LMDB.mdb_txn_begin(env, MemoryUtil.NULL, 0, pp));
			long txn = pp.get(0);

			int err;
			try {
				ret = transaction.exec(stack, txn);
				err = LMDB.mdb_txn_commit(txn);
			} catch (Throwable t) {
				LMDB.mdb_txn_abort(txn);
				throw t;
			}
			E(err);
		}

		return ret;
	}

	static int openDatabase(long env) {
		return transaction(env, (stack, txn) -> {
			IntBuffer ip = stack.mallocInt(1);

			E(LMDB.mdb_dbi_open(txn, (CharSequence)null, LMDB.MDB_INTEGERKEY, ip));
			return ip.get(0);
		});
	}

}
