package com.abu.xbase.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.HashSet;
import java.util.Map;

/**
 * SharePreferences操作工具类
 */
public class SharePrefUtil {
	private static String tag = SharePrefUtil.class.getSimpleName();
	private final static String SP_NAME = "spref";
	private static SharedPreferences sp;
	public final static byte[] hex = "0123456789abcdef".getBytes();

	public static String Bytes2HexString(byte[] bb)
	{
		int i,len;
		len=bb.length;
		byte[] b=new byte[len];
		for (i=0;i<len;i++) {
			b[i] = bb[len - i - 1];
		}

		byte[] buff = new byte[2 * b.length];

		for ( i = 0; i < b.length; i++) {

			buff[2 * i] = hex[(b[b.length - i - 1] >> 4) & 0x0f];
			buff[2 * i + 1] = hex[b[b.length - i - 1] & 0x0f];
		}
		return new String(buff);
	}
	public static byte[] string2bytes(String s){
		String ss = s.replace(" ", "");
		int string_len = ss.length();
		int len = string_len/2;
		if(string_len%2 ==1){
			ss = "0"+ss;
			string_len++;
			len++;
		}
		byte[] a = new byte[len];
		try {
			for (int i = 0; i < len; i++) {
				a[i] = (byte) Integer.parseInt(ss.substring(2 * i, 2 * i + 2), 16);
			}
		}catch (NumberFormatException e){
			return null;
		}
		return a;
	}


	public static <T> void saveObj(Context context, T obj, String key) {
		if (sp == null) {
			sp = context.getSharedPreferences(SP_NAME, 0);
		}
		if (obj==null) {
			sp.edit().remove(key).apply();
			return;
		}
		if(!(obj instanceof JsonObject) && !(obj instanceof Serializable)) {
			throw new RuntimeException(" the obj must instanceof Serializable or Gson.JsonObject who want to be save");
		}
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			if(obj instanceof JsonObject){
				oos.writeObject(obj.toString());
			} else {
				oos.writeObject(obj);
			}
			String oosStr = Bytes2HexString(baos.toByteArray());
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(key, oosStr);
			editor.apply();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(oos != null){
					oos.close();
				}
				if(baos!= null) {
					baos.close();
				}
			} catch (IOException e1) {

			}
		}
		return;
	}

	public static <T> T readObj(Context context, String key, Class<T> tClass) {
		T obj = null;
		try {
			obj = tClass.newInstance();
			if(!(obj instanceof JsonObject) && !(obj instanceof Serializable)) {
				throw new RuntimeException(" the obj must instanceof Serializable or Gson.JsonObject who want to be save");
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		if (sp == null) {
			sp = context.getSharedPreferences(SP_NAME, 0);
		}
		String oosStr = sp.getString(key, null);
		if(oosStr == null) {
			return null;
		}
		byte[] oosBytes = string2bytes(oosStr);
		if(oosBytes == null) {
			return null;
		}
		ByteArrayInputStream bais = null;
		ObjectInputStream bis = null;
		try {
			bais = new ByteArrayInputStream(oosBytes);
			bis = new ObjectInputStream(bais);
			if(obj != null && obj instanceof JsonObject){
				return new Gson().fromJson((String)bis.readObject(), tClass);
			} else {
				return tClass.cast(bis.readObject());
			}

		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ClassCastException e){
			e.printStackTrace();
		} finally{
			try {
				if (bis != null) {
					bis.close();
				}
				if (bais != null) {
					bais.close();
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 保存布尔值
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveBoolean(Context context, String key, boolean value) {
		if (sp == null){sp = context.getSharedPreferences(SP_NAME, 0);}

		sp.edit().putBoolean(key, value).apply();
	}

	public static void saveStringSet(Context context, String key, HashSet<String> value) {
		saveObj(context, value, key);
	}

	public static HashSet<String> getStringSet(Context context, String key, HashSet<String> defValue) {
		HashSet<String> obj = null;
		try {
			obj = (HashSet<String>) readObj(context, key, new HashSet<String>().getClass());
		}catch (Exception e){}
		if(obj == null) {
			return defValue;
		}
		return obj;
	}

	/**
	 * 保存字符串
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveString(Context context, String key, String value) {
		if (sp == null){sp = context.getSharedPreferences(SP_NAME, 0);}

		sp.edit().putString(key, value).apply();
	}

	public static void saveString(Context context, Map<String, String> key_value){
		if(sp == null){sp = context.getSharedPreferences(SP_NAME, 0);}

		SharedPreferences.Editor editor = sp.edit();
		for(String key : key_value.keySet())
		{
			editor.putString(key, key_value.get(key));
		}
		editor.apply();
	}

	public static void clear(Context context) {
		if (sp == null) {
			sp = context.getSharedPreferences(SP_NAME, 0);
		}
		sp.edit().clear().apply();
	}

	public static void removeKey(Context context, String key) {
		if (sp == null) {
			sp = context.getSharedPreferences(SP_NAME, 0);
		}
		sp.edit().remove(key).apply();
	}

	/**
	 * 保存long型
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveLong(Context context, String key, long value) {
		if (sp == null){sp = context.getSharedPreferences(SP_NAME, 0);}

		sp.edit().putLong(key, value).apply();
	}

	/**
	 * 保存int型
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveInt(Context context, String key, int value) {
		if (sp == null) {
			sp = context.getSharedPreferences(SP_NAME, 0);
		}
		sp.edit().putInt(key, value).apply();
	}

	/**
	 * 保存float型
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveFloat(Context context, String key, float value) {
		if (sp == null){sp = context.getSharedPreferences(SP_NAME, 0);}

		sp.edit().putFloat(key, value).apply();
	}

	/**
	 * 获取字符值
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static String getString(Context context, String key, String defValue) {
		if (sp == null){sp = context.getSharedPreferences(SP_NAME, 0);}

		return sp.getString(key, defValue);
	}

	/**
	 * 获取int值
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static int getInt(Context context, String key, int defValue) {
		if (sp == null){sp = context.getSharedPreferences(SP_NAME, 0);}

		return sp.getInt(key, defValue);
	}

	/**
	 * 获取long值
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static long getLong(Context context, String key, long defValue) {
		if (sp == null){sp = context.getSharedPreferences(SP_NAME, 0);}

		return sp.getLong(key, defValue);
	}

	/**
	 * 获取float值
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static float getFloat(Context context, String key, float defValue) {
		if (sp == null){
			sp = context.getSharedPreferences(SP_NAME, 0);
		}
		return sp.getFloat(key, defValue);
	}

	/**
	 * 获取布尔值
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static boolean getBoolean(Context context, String key, boolean defValue) {
		if (sp == null) {
			sp = context.getSharedPreferences(SP_NAME, 0);
		}
		return sp.getBoolean(key, defValue);
	}




}
