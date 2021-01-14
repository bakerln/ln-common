package common.framework.util;

import java.io.*;

/**
 * <p>Description: 序列化工具</p>
 *
 * @author linan
 * @date 2020-10-21
 */
public class SerializeUtil {
	/**
	 * 序列化
	 * @param object 对象
	 * @return 字节
	 */
	public static byte[] serialize(Object object) throws IOException {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}finally {
			oos.close();
			baos.close();
		}
	}

	/**
	 * 反序列化
	 * @param bytes 字节
	 * @return 对象
	 */
	public static Object deserialize(byte[] bytes) throws IOException {
		ByteArrayInputStream bais = null;
		try {
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}finally {
			bais.close();
		}
	}
}
