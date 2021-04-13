package ru.redguy.tftpserver;

import ru.redguy.tftpserver.datasource.FileSystem;
import ru.redguy.tftpserver.datasource.IDataSource;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class TFTPServer {

    private DatagramSocket socket;
    private Runner runner;
    private Thread thread;
    private ErrorEvent errorEvent;
    private IDataSource dataSource;

    public void start() throws SocketException {
        start(69,true);
    }

    public void start(int port) throws SocketException {
        start(port,true);
    }

    public void start(int port, boolean isDaemon) throws SocketException {
        start(port, isDaemon, new FileSystem("."));
    }

    public void start(int port, boolean isDaemon, IDataSource dataSource) throws SocketException {
        this.dataSource = dataSource;
        socket = new DatagramSocket(port);
        runner = new Runner(socket, this);
        thread = new Thread(runner);
        thread.setDaemon(isDaemon);
        thread.start();
    }

    public void start(String host, int port) throws UnknownHostException, SocketException {
        start(host, port, new FileSystem("."));
    }

    public void start(String host, int port, IDataSource dataSource) throws UnknownHostException, SocketException {
        this.dataSource = dataSource;
        socket = new DatagramSocket(port, InetAddress.getByName(host));
        runner = new Runner(socket, this);
        thread.setDaemon(true);
        thread.start();
    }

    public int getPort() {
        return socket.getLocalPort();
    }

    public void stop() {
        thread.interrupt();
    }

    public void onError(ErrorEvent event) {
        this.errorEvent = event;
    }

    static class Runner implements Runnable {

        DatagramSocket datagramSocket;
        TFTPServer server;
        boolean run = true;

        public Runner(DatagramSocket socket, TFTPServer server) {
            this.datagramSocket = socket;
            this.server = server;
        }

        public void stop() {
            run = false;
        }

        @Override
        public void run() {
            while (run) {
                TFTPpacket in = null;
                try {
                    in = TFTPpacket.receive(datagramSocket);
                } catch (IOException e) {
                    server.errorEvent.onPacketReceiveException(e);
                }

                if (in instanceof TFTPread) {
                    try {
                        TFTPserverRRQ r = new TFTPserverRRQ((TFTPread) in, server.errorEvent, server.dataSource);
                    } catch (TftpException e) {
                        server.errorEvent.onPacketReadException(e);
                    }
                } else if (in instanceof TFTPwrite) {
                    try {
                        TFTPserverWRQ w = new TFTPserverWRQ((TFTPwrite) in, server.errorEvent, server.dataSource);
                    } catch (TftpException e) {
                        server.errorEvent.onPacketWriteException(e);
                    }
                }
            }
        }
    }
}