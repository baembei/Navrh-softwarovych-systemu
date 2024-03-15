package nss.cviceni2.compute;

import java.rmi.Remote;
import java.rmi.RemoteException;

import nss.cviceni2.server.DBExistException;
import nss.cviceni2.server.DBNotFoundException;
import nss.cviceni2.server.DuplicateKeyException;
import nss.cviceni2.server.KeyNotFoundException;


public interface ServerInterface extends Remote {

    /**
     * vypise existujici databaze
     */
    public String[] listDB() throws RemoteException;

    /**
     * vytvori databazi daneho jmena
     */
    public boolean createDB(String dbname) throws DBExistException, RemoteException;

    /**
     * vytvori v databazi novy zaznam
     */
    public Integer insert(String dbname, Integer key, String message) throws DBNotFoundException, DuplicateKeyException, RemoteException;

    /**
     * aktualizuje zaznam dany klicem key na hodnotu message
     */
    public Integer update(String dbname, Integer key, String message) throws DBNotFoundException, KeyNotFoundException, RemoteException;

    /**
     * vrati zpravu prislusejici danemu klici
     */
    public DBRecord get(String dbname, Integer key) throws DBNotFoundException, KeyNotFoundException, RemoteException;

    /**
     * vrati pole zaznamu prislusejici danym klicum, operace se povede bez chyby
     * pouze tehdy, pokud se povedou najit vsechny odpovedi
     */
    public DBRecord[] getA(String dbname, Integer[] key) throws DBNotFoundException, KeyNotFoundException, RemoteException;

    /**
     * zapise zmeny na disk
     */
    public void flush() throws RemoteException;
}
