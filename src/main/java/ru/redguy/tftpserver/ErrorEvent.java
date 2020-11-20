package ru.redguy.tftpserver;

public interface ErrorEvent {
    public void onPacketReceiveException(Exception exception);

    public void onPacketReadException(Exception exception);

    public void onPacketWriteException(Exception exception);

    public void onClientReadException(Exception exception, TFTPread tftPread);

    public void onClientWriteException(Exception exception, TFTPwrite tftPwrite);
}
