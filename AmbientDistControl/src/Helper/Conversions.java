/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Patze
 */
public class Conversions  {

  public static double arr2double(byte arr[], int start) {
    int i = 0;
    int len = 8;
    int cnt = 0;
    byte tmp[] = new byte[len];
    long accum = 0L;
    for (i = start; i < start + len; i++) {
      tmp[cnt] = arr[i];
      cnt++;
    }

    i = 0;
    for (int shiftBy = 0; shiftBy < 64; shiftBy += 8) {
      accum |= (long) (tmp[i] & 0xff) << shiftBy;
      i++;
    }

    return Double.longBitsToDouble(accum);
  }

  public static float arr2float(byte arr[], int start) {
    int i = 0;
    int len = 4;
    int cnt = 0;
    byte tmp[] = new byte[len];
    int accum = 0;
    for (i = start; i < start + len; i++) {
      tmp[cnt] = arr[i];
      cnt++;
    }

    i = 0;
    for (int shiftBy = 0; shiftBy < 32; shiftBy += 8) {
      accum = (int) ((long) accum | (long) (tmp[i] & 0xff) << shiftBy);
      i++;
    }

    return Float.intBitsToFloat(accum);
  }

  public static int arr2int(byte arr[], int start) {
    int low = arr[start] & 0xff;
    int high = arr[start + 1] & 0xff;
    return high << 8 | low;
  }

  public static byte[] float2ByteArray(float value) {
    return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(value).array();
  }
}
