package jp.co.rakuten.rit.roma.client.util;

import java.util.Properties;

import jp.co.rakuten.rit.roma.client.AllTests;
import jp.co.rakuten.rit.roma.client.Node;
import jp.co.rakuten.rit.roma.client.RomaClient;
import jp.co.rakuten.rit.roma.client.RomaClientFactory;
import jp.co.rakuten.rit.roma.client.commands.TimeoutException;

import junit.framework.TestCase;

public class ListWrapperPerfTest extends TestCase {
  private static String NODE_ID = AllTests.NODE_ID;

  private static String KEY_PREFIX = ListWrapperPerfTest.class.getName();

  private static RomaClient CLIENT = null;

  private static ListWrapper LISTUTIL = null;

  public static int BIG_LOOP_COUNT = 100;

  public static int SMALL_LOOP_COUNT = 1000;

  public static int SIZE_OF_DATA = 30;

  public static int NUM_OF_THREADS = 1;

  public static long PERIOD_OF_SLEEP = 10;

  public static long PERIOD_OF_TIMEOUT = 5000;

  public ListWrapperPerfTest() {
    super();
  }

  @Override
  public void setUp() throws Exception {
    RomaClientFactory factory = RomaClientFactory.getInstance();
    CLIENT = factory.newRomaClient(new Properties());
    LISTUTIL = new ListWrapper(CLIENT);
    CLIENT.open(Node.create(NODE_ID));
    CLIENT.setTimeout(PERIOD_OF_TIMEOUT);
  }

  @Override
  public void tearDown() throws Exception {
    LISTUTIL = null;
    CLIENT.close();
    CLIENT = null;
  }

  public void testDummy() {
    assertTrue(true);
  }

  public void XtestDeleteAndPrependLoop01() throws Exception {
    big_loop();
    assertTrue(true);
  }

  public void XtestDeleteAndPrependLoop02() throws Exception {
    Thread[] threads = new Thread[NUM_OF_THREADS];
    for (int i = 0; i < threads.length; ++i) {
      threads[i] = new Thread() {
        @Override
        public void run() {
          try {
            big_loop();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      };
    }
    for (int i = 0; i < threads.length; ++i) {
      threads[i].start();
    }

    while (true) {
      Thread.sleep(1000);
    }
  }

  private void big_loop() throws Exception {
    int count = 0;
    while (count < BIG_LOOP_COUNT) {
      small_loop(count);
      count++;
    }
  }

  private void small_loop(int big_count) throws Exception {
    int count = 0;
    int count_threshold = 0;
    int count_threshold1 = 0;
    String dummy_prefix = makeDummyPrefix();
    long time0 = System.currentTimeMillis();
    while (count < SMALL_LOOP_COUNT) {
      if (count % 1000 == 0) {
        System.out.println("count: " + count);
      }
      try {
        long time = System.currentTimeMillis();
        LISTUTIL.deleteAndPrepend(KEY_PREFIX + count, (dummy_prefix + count)
            .getBytes());
        // LISTUTIL.get(KEY_PREFIX + count);
        time = System.currentTimeMillis() - time;
        if (time > PERIOD_OF_TIMEOUT) {
          count_threshold++;
        }
      } catch (TimeoutException e) {
        count_threshold1++;
        System.out.println(e.getMessage());
        // e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      } finally {
        Thread.sleep(PERIOD_OF_SLEEP);
        count++;
      }
    }
    time0 = System.currentTimeMillis() - time0;

    StringBuilder sb = new StringBuilder();
    sb.append("qps: ").append(
        (int) (((double) (SMALL_LOOP_COUNT * 1000)) / time0)).append(" ")
        .append("(timeout count: ").append(count_threshold).append(", ")
        .append(count_threshold1).append(")");

    System.out.println(sb.toString());
  }

  private static final char A = 'a';

  private static String makeDummyPrefix() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < SIZE_OF_DATA; ++i) {
      sb.append(A);
    }
    sb.append("::");
    return sb.toString();
  }
}