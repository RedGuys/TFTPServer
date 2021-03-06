package ru.redguy.tftpserver;

import ru.redguy.tftpserver.datasource.IDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;


class TFTPserverRRQ extends Thread {

    protected DatagramSocket sock;
    protected InetAddress host;
    protected int port;
    protected InputStream source;
    protected TFTPpacket req;
    protected int timeoutLimit = 5;
    protected String fileName;
    protected IDataSource dataSource;

    // initialize read request
    public TFTPserverRRQ(TFTPread request, ErrorEvent event, IDataSource dataSource) throws TftpException {
        try {
            this.dataSource = dataSource;
            req = request;
            //open new socket with random port num for tranfer
            sock = new DatagramSocket();
            sock.setSoTimeout(1000);
            fileName = request.fileName();

            host = request.getAddress();
            port = request.getPort();

            //check file
            if (dataSource.isFileExists(fileName) && dataSource.isFile(fileName) && dataSource.isCanRead(fileName)) {
                source = dataSource.getInputStream(fileName);
                this.start(); //open new thread for transfer
            } else
                throw new TftpException("access violation");

        } catch (Exception e) {
            TFTPerror ePak = new TFTPerror(1, e.getMessage()); // error code 1
            try {
                ePak.send(host, port, sock);
            } catch (Exception f) {
            }

            event.onClientReadException(e, request);
        }
    }

    //everything is fine, open new thread to transfer file
    public void run() {
        int bytesRead = TFTPpacket.maxTftpPakLen;
        // handle read request
        if (req instanceof TFTPread) {
            try {
                for (int blkNum = 1; bytesRead == TFTPpacket.maxTftpPakLen; blkNum++) {
                    TFTPdata outPak = new TFTPdata(blkNum, source);
                    /*System.out.println("send block no. " + outPak.blockNumber()); */
                    bytesRead = outPak.getLength();
                    /*System.out.println("bytes sent:  " + bytesRead);*/
                    outPak.send(host, port, sock);
                    /*System.out.println("current op code  " + outPak.get(0)); */

                    //wait for the correct ack. if incorrect, retry up to 5 times
                    while (timeoutLimit != 0) {
                        try {
                            TFTPpacket ack = TFTPpacket.receive(sock);
                            if (!(ack instanceof TFTPack)) {
                                throw new Exception("Client failed");
                            }
                            TFTPack a = (TFTPack) ack;

                            if (a.blockNumber() != blkNum) { //check ack
                                throw new SocketTimeoutException("last packet lost, resend packet");
                            }
                            /*System.out.println("confirm blk num " + a.blockNumber()+" from "+a.getPort());*/
                            break;
                        } catch (SocketTimeoutException t) {//resend last packet
                            System.out.println("Resent blk " + blkNum);
                            timeoutLimit--;
                            outPak.send(host, port, sock);
                        }
                    } // end of while
                    if (timeoutLimit == 0) {
                        throw new Exception("connection failed");
                    }
                }
                System.out.println("Transfer completed.(Client " + host + ")");
                System.out.println("Filename: " + fileName + "\nSHA1 checksum: " + CheckSum.getChecksum(fileName) + "\n");
            } catch (Exception e) {
                TFTPerror ePak = new TFTPerror(1, e.getMessage());

                try {
                    ePak.send(host, port, sock);
                } catch (Exception f) {
                }

                System.out.println("Client failed:  " + e.getMessage());
            }
        }
    }
}