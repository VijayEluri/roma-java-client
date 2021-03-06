package jp.co.rakuten.rit.roma.client.util.commands;

import jp.co.rakuten.rit.roma.client.ClientException;
import jp.co.rakuten.rit.roma.client.commands.CommandContext;

public class ExpiredSwapAndSizedInsertCommand extends UpdateCommand {
  @Override
  protected void create(CommandContext context) throws ClientException {
    // alist_expired_swap_and_sized_insert <key> <expire-time> <array-size>
    // <bytes>\r\n
    // <value>\r\n
    StringBuilder sb = (StringBuilder) context.get(CommandContext.STRING_DATA);
    sb.append(ListCommandID.STR_ALIST_EXPIRED_SWAP_AND_SIZED_INSERT)
      .append(ListCommandID.STR_WHITE_SPACE)
      .append(context.get(CommandContext.KEY))
      .append(ListCommandID.STR_WHITE_SPACE)
      .append(context.get(UpdateCommand.EXPIRY))
      .append(ListCommandID.STR_WHITE_SPACE)
      .append(context.get(UpdateCommand.ARRAY_SIZE))
      .append(ListCommandID.STR_WHITE_SPACE)
      .append(((byte[]) context.get(CommandContext.VALUE)).length)
      .append(ListCommandID.STR_CRLF);
    context.put(CommandContext.STRING_DATA, sb);
  }
}