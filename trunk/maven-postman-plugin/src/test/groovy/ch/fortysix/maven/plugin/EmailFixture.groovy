package ch.fortysix.maven.plugin;

import org.junit.Assert;
import org.subethamail.wiser.Wiser

public class EmailFixture {

    private wiser = new Wiser()
    EmailFixture(int port) {
        wiser.port = port
        wiser.start()
    }

    def assertEmailArrived(String from, String subject) {
        wiser.stop()
        assert wiser.messages.size() != 0, 'No messages arrived!'
        def message = wiser.messages[0].mimeMessage
        Assert.assertEquals(from, message?.from[0].toString())
        Assert.assertEquals(subject, message?.subject)
    }

}
