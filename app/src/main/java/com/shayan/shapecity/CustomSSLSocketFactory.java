package com.shayan.shapecity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class CustomSSLSocketFactory extends SSLSocketFactory{


    private final SSLSocketFactory factory;



    public CustomSSLSocketFactory(){
        this.factory = HttpsURLConnection.getDefaultSSLSocketFactory();
    }

    public CustomSSLSocketFactory(SSLSocketFactory factory){
        this.factory = factory;
    }


    @Override
    public String[] getDefaultCipherSuites(){
        return factory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites(){
        return factory.getSupportedCipherSuites();
    }

    private Socket makeSocketSafe(Socket socket){
        if(socket instanceof SSLSocket){
            socket = new CustomSSLSocket((SSLSocket) socket);
        }

        return socket;
    }

    @Override
    public Socket createSocket() throws IOException{
        return makeSocketSafe(factory.createSocket());
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException{
        return makeSocketSafe(factory.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException{
        return makeSocketSafe(factory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException{
        return makeSocketSafe(factory.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException{
        return makeSocketSafe(factory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException{
        return makeSocketSafe(factory.createSocket(address, port, localAddress, localPort));
    }




    public class CustomSSLSocket extends DelegateSSLSocket{

        private CustomSSLSocket(SSLSocket delegate){
            super(delegate);

        }

        @Override
        public void setEnabledProtocols(String[] protocols){
//            List<String> enabledProtocols = new ArrayList<>(Arrays.asList(socket.getEnabledProtocols()));
//            enabledProtocols.removeEntries("SSLv2");
//            enabledProtocols.removeEntries("SSLv3");
//            enabledProtocols.removeEntries("TLSv1.1");
//            enabledProtocols.removeEntries("TLSv1");
//            enabledProtocols.removeEntries("TLSv1.2");
//            super.setEnabledProtocols(enabledProtocols.toArray(new String[enabledProtocols.size()]));
            super.setEnabledProtocols(new String[]{ "TLSv1.2" });
        }

        @Override
        public void setEnabledCipherSuites(String[] suites) {
            super.setEnabledCipherSuites(new String[]{ "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256" });
        }
    }

    public class DelegateSSLSocket extends SSLSocket{

        protected final SSLSocket socket;



        DelegateSSLSocket(SSLSocket delegate){
            this.socket = delegate;
        }





        @Override
        public String[] getSupportedCipherSuites(){
            return socket.getSupportedCipherSuites();
        }

        @Override
        public String[] getEnabledCipherSuites(){
            return socket.getEnabledCipherSuites();
        }

        @Override
        public void setEnabledCipherSuites(String[] suites){
            socket.setEnabledCipherSuites(suites);
        }

        @Override
        public String[] getSupportedProtocols(){
            return socket.getSupportedProtocols();
        }

        @Override
        public String[] getEnabledProtocols(){
            return socket.getEnabledProtocols();
        }

        @Override
        public void setEnabledProtocols(String[] protocols){
            socket.setEnabledProtocols(protocols);
        }

        @Override
        public SSLSession getSession(){
            return socket.getSession();
        }

        @Override
        public void addHandshakeCompletedListener(HandshakeCompletedListener listener){
            socket.addHandshakeCompletedListener(listener);
        }

        @Override
        public void removeHandshakeCompletedListener(HandshakeCompletedListener listener){
            socket.removeHandshakeCompletedListener(listener);
        }

        @Override
        public void startHandshake() throws IOException{
            socket.startHandshake();
        }

        @Override
        public void setUseClientMode(boolean mode){
            socket.setUseClientMode(mode);
        }

        @Override
        public boolean getUseClientMode(){
            return socket.getUseClientMode();
        }

        @Override
        public void setNeedClientAuth(boolean need){
            socket.setNeedClientAuth(need);
        }

        @Override
        public void setWantClientAuth(boolean want){
            socket.setWantClientAuth(want);
        }

        @Override
        public boolean getNeedClientAuth(){
            return socket.getNeedClientAuth();
        }

        @Override
        public boolean getWantClientAuth(){
            return socket.getWantClientAuth();
        }

        @Override
        public void setEnableSessionCreation(boolean flag){
            socket.setEnableSessionCreation(flag);
        }

        @Override
        public boolean getEnableSessionCreation(){
            return socket.getEnableSessionCreation();
        }

        @Override
        public void bind(SocketAddress localAddr) throws IOException{
            socket.bind(localAddr);
        }

        @Override
        public synchronized void close() throws IOException{
            socket.close();
        }

        @Override
        public void connect(SocketAddress remoteAddr) throws IOException{
            socket.connect(remoteAddr);
        }

        @Override
        public void connect(SocketAddress remoteAddr, int timeout) throws IOException{
            socket.connect(remoteAddr, timeout);
        }

        @Override
        public SocketChannel getChannel(){
            return socket.getChannel();
        }

        @Override
        public InetAddress getInetAddress(){
            return socket.getInetAddress();
        }

        @Override
        public InputStream getInputStream() throws IOException{
            return socket.getInputStream();
        }

        @Override
        public boolean getKeepAlive() throws SocketException{
            return socket.getKeepAlive();
        }

        @Override
        public InetAddress getLocalAddress(){
            return socket.getLocalAddress();
        }

        @Override
        public int getLocalPort(){
            return socket.getLocalPort();
        }

        @Override
        public SocketAddress getLocalSocketAddress(){
            return socket.getLocalSocketAddress();
        }

        @Override
        public boolean getOOBInline() throws SocketException{
            return socket.getOOBInline();
        }

        @Override
        public OutputStream getOutputStream() throws IOException{
            return socket.getOutputStream();
        }

        @Override
        public int getPort(){
            return socket.getPort();
        }

        @Override
        public synchronized int getReceiveBufferSize() throws SocketException{
            return socket.getReceiveBufferSize();
        }

        @Override
        public SocketAddress getRemoteSocketAddress(){
            return socket.getRemoteSocketAddress();
        }

        @Override
        public boolean getReuseAddress() throws SocketException{
            return socket.getReuseAddress();
        }

        @Override
        public synchronized int getSendBufferSize() throws SocketException{
            return socket.getSendBufferSize();
        }

        @Override
        public int getSoLinger() throws SocketException{
            return socket.getSoLinger();
        }

        @Override
        public synchronized int getSoTimeout() throws SocketException{
            return socket.getSoTimeout();
        }

        @Override
        public boolean getTcpNoDelay() throws SocketException{
            return socket.getTcpNoDelay();
        }

        @Override
        public int getTrafficClass() throws SocketException{
            return socket.getTrafficClass();
        }

        @Override
        public boolean isBound(){
            return socket.isBound();
        }

        @Override
        public boolean isClosed(){
            return socket.isClosed();
        }

        @Override
        public boolean isConnected(){
            return socket.isConnected();
        }

        @Override
        public boolean isInputShutdown(){
            return socket.isInputShutdown();
        }

        @Override
        public boolean isOutputShutdown(){
            return socket.isOutputShutdown();
        }

        @Override
        public void sendUrgentData(int value) throws IOException{
            socket.sendUrgentData(value);
        }

        @Override
        public void setKeepAlive(boolean keepAlive) throws SocketException{
            socket.setKeepAlive(keepAlive);
        }

        @Override
        public void setOOBInline(boolean oobinline) throws SocketException{
            socket.setOOBInline(oobinline);
        }

        @Override
        public void setPerformancePreferences(int connectionTime, int latency, int bandwidth){
            socket.setPerformancePreferences(connectionTime, latency, bandwidth);
        }

        @Override
        public synchronized void setReceiveBufferSize(int size) throws SocketException{
            socket.setReceiveBufferSize(size);
        }

        @Override
        public void setReuseAddress(boolean reuse) throws SocketException{
            socket.setReuseAddress(reuse);
        }

        @Override
        public synchronized void setSendBufferSize(int size) throws SocketException{
            socket.setSendBufferSize(size);
        }

        @Override
        public void setSoLinger(boolean on, int timeout) throws SocketException{
            socket.setSoLinger(on, timeout);
        }

        @Override
        public synchronized void setSoTimeout(int timeout) throws SocketException{
            socket.setSoTimeout(timeout);
        }

        @Override
        public void setTcpNoDelay(boolean on) throws SocketException{
            socket.setTcpNoDelay(on);
        }

        @Override
        public void setTrafficClass(int value) throws SocketException{
            socket.setTrafficClass(value);
        }

        @Override
        public void shutdownInput() throws IOException{
            socket.shutdownInput();
        }

        @Override
        public void shutdownOutput() throws IOException{
            socket.shutdownOutput();
        }

        @Override
        public String toString(){
            return socket.toString();
        }

        @Override
        public boolean equals(Object o){
            return socket.equals(o);
        }
    }
}