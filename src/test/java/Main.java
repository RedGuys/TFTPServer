import ru.redguy.tftpserver.ErrorEvent;
import ru.redguy.tftpserver.TFTPServer;
import ru.redguy.tftpserver.TFTPread;
import ru.redguy.tftpserver.TFTPwrite;

import java.net.SocketException;

public class Main {
    public static void main(String[] args) {
        TFTPServer tftpServer = new TFTPServer();
        try {
            tftpServer.start(1000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        System.out.println(tftpServer.getPort());
        tftpServer.onError(new ErrorEvent() {
            @Override
            public void onPacketReceiveException(Exception exception) {
                exception.printStackTrace();
            }

            @Override
            public void onPacketReadException(Exception exception) {
                exception.printStackTrace();
            }

            @Override
            public void onPacketWriteException(Exception exception) {
                exception.printStackTrace();
            }

            @Override
            public void onClientReadException(Exception exception, TFTPread tftPread) {
                exception.printStackTrace();
                System.out.println(tftPread.fileName());
            }

            @Override
            public void onClientWriteException(Exception exception, TFTPwrite tftPwrite) {
                exception.printStackTrace();
                System.out.println(tftPwrite.fileName());
            }
        });
        while (true) {}
    }
}
