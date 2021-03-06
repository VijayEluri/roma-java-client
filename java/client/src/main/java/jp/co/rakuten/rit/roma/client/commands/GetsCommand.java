package jp.co.rakuten.rit.roma.client.commands;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import jp.co.rakuten.rit.roma.client.ClientException;
import jp.co.rakuten.rit.roma.client.Connection;

public class GetsCommand extends AbstractCommand {
  @SuppressWarnings("unchecked")
  @Override
  public boolean execute(CommandContext context) throws ClientException {
    try {
      // "gets <key>*\r\n"
      StringBuilder sb = new StringBuilder();
      List<String> keys = (List<String>) context.get(CommandContext.KEYS);

      sb.append(STR_GETS);
      for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
        sb.append(STR_WHITE_SPACE)
          .append(iter.next())
          .append(STR_ESC)
          .append(context.get(CommandContext.HASH_NAME));
      }
      sb.append(STR_CRLF);

      Connection conn = (Connection) context.get(CommandContext.CONNECTION);
      conn.out.write(sb.toString().getBytes());
      conn.out.flush();

      // VALUE foo 0 <valueLen>\r\n<value>\r\n or END\r\n
      Map<String, byte[]> values = null;
      String s;
      s = conn.in.readLine();
      if (s.startsWith("VALUE")) {
        values = new HashMap<String, byte[]>();
      } else if (s.startsWith("END")) {
        return false;
      } else if (s.startsWith("SERVER_ERROR") || s.startsWith("CLIENT_ERROR")
          || s.startsWith("ERROR")) {
        throw new ClientException(s);
      } else {
        throw new ClientException("Not supported yet.");
      }

      do {
        StringTokenizer t = new StringTokenizer(s);
        t.nextToken(); // VALUE
        String key = t.nextToken(); // key
        t.nextToken(); // 0
        int valueLen = Integer.parseInt(t.nextToken()); // len

        // value
        byte[] value = new byte[valueLen];
        int offset = 0;
        int size = 0;
        while (offset < valueLen) {
          size = conn.in.read(value, offset, valueLen - offset);
          offset = offset + size;
        }
        conn.in.read(2); // "\r\n"
        values.put(key, value);

        s = conn.in.readLine();
      } while (!s.equals("END"));

      context.put(CommandContext.RESULT, values);
      return true;
    } catch (IOException e) {
      throw new ClientException(e);
    }
  }

  @Override
  protected void sendAndReceive(CommandContext context) throws IOException,
      ClientException {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void create(CommandContext context) throws ClientException {
    throw new UnsupportedOperationException();
  }

  @Override
  protected boolean parseResult(CommandContext context) throws ClientException {
    throw new UnsupportedOperationException();
  }
}