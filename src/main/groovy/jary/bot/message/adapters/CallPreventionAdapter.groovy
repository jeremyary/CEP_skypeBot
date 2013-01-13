package jary.bot.message.adapters

import com.skype.Call
import com.skype.CallAdapter
import com.skype.SkypeException
import org.springframework.stereotype.Component

/**
 * prevent incoming and outgoing calls to/from bot
 *
 * @author jary
 * @since 01/13/2013
 */
@Component
class CallPreventionAdapter extends CallAdapter {

    @Override
    public void callMaked(Call outbound) throws SkypeException {
        outbound.finish()
    }

    @Override
    public void callReceived(Call inbound) throws SkypeException {
        inbound.finish()
    }
}
