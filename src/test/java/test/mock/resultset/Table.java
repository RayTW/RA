package test.mock.resultset;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/** Test class. */
public class Table {
  private Map<Integer, String> columnIndex;
  private Map<String, List<byte[]>> data;
  private static final byte[] ZERO_BYTE = new byte[0];

  /**
   * 初始資料表欄位名稱.
   *
   * @param columnLabel 資料表欄位名稱
   */
  public Table(String... columnLabel) {
    data = new ConcurrentHashMap<>();
    columnIndex = new ConcurrentHashMap<>();

    for (int i = 0; i < columnLabel.length; i++) {
      String columnName = columnLabel[i];
      data.put(columnName, new CopyOnWriteArrayList<byte[]>());
      columnIndex.put(Integer.valueOf(i), columnName);
    }
  }

  public void put(String columnName, int value) {
    put(columnName, String.valueOf(value).getBytes());
  }

  public void put(String columnName, long value) {
    put(columnName, String.valueOf(value).getBytes());
  }

  public void put(String columnName, short value) {
    put(columnName, String.valueOf(value).getBytes());
  }

  public void put(String columnName, String value) {
    put(columnName, value == null ? null : value.getBytes());
  }

  public void put(String columnName, float value) {
    put(columnName, String.valueOf(value).getBytes());
  }

  public void put(String columnName, double value) {
    put(columnName, String.valueOf(value).getBytes());
  }

  public void put(String columnName, BigDecimal value) {
    put(columnName, value == null ? ZERO_BYTE : value.toString().getBytes());
  }

  /**
   * 指定欄位並新增資料.
   *
   * @param columnName 欄位名稱
   * @param value 資料
   */
  public void put(String columnName, byte[] value) {
    List<byte[]> list = data.get(columnName);

    if (list == null) {
      throw new IllegalArgumentException(
          "The specified key does not exist ,columnName = '" + columnName + "' ");
    }
    list.add(value);
  }

  /**
   * 取得指定欄位眾料.
   *
   * @param column 欄位名稱索引
   * @param cursor 資料筆索引
   */
  public byte[] getBytes(int column, int cursor) {
    String columnName = columnIndex.get(column);
    List<byte[]> columnData = data.get(columnName);

    if (cursor < columnData.size()) {
      return columnData.get(cursor);
    }

    return ZERO_BYTE;
  }

  public int size() {
    return data.values().stream().map(list -> list.size()).reduce(Math::max).get();
  }
}
