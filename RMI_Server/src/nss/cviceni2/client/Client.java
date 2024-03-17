package nss.cviceni2.client;

import java.io.FileNotFoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import com.sun.management.DiagnosticCommandMBean;
import nss.cviceni2.compute.DBRecord;
import nss.cviceni2.compute.ServerInterface;
import nss.cviceni2.server.CsvReader;
import nss.cviceni2.server.DBExistException;
import nss.cviceni2.server.DBNotFoundException;

public class Client {

	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}

		String host;
		int PORT;
		String conf_file;

		if (args.length != 3) {
			// <IP_adresa> <cislo_portu> <konf_soubor>
			host = "localhost"; //host = "127.0.0.1";
			PORT = 7777; 
			conf_file = "./data/conf.csv"; // konfig. soubor
		} else {
			host = args[0]; 
			PORT = Integer.valueOf(args[1]); 
			conf_file = args[2]; 
		}

		try {
			// vyhledani vzdaleneho objektu
			ServerInterface serveri;
			String name = "muj_server";
			Registry registry = LocateRegistry.getRegistry(host, PORT);

			 serveri = (ServerInterface) registry.lookup(name);

			 //TODO HERE continue.. :-(  :-(  :-(  :-(  :-(  :-(  :-(
			CsvReader reader = new CsvReader(conf_file, ';');
			while (reader.readRecord()) {
				String tscreate = reader.get(0);
				System.out.println(">> " + tscreate);
				switch (tscreate) {
					case "listdb":
						String[] databases = serveri.listDB();
						System.out.println("<< Databases:");
						for (String db : databases) {
							System.out.println("- " + db);
						}
						break;
					case "createdb":
						String dbName = reader.get(1);
						try {
							boolean created = serveri.createDB(dbName);
							if (created) {
								System.out.println("<< Database \"" + dbName + "\" created");
							} else {
								System.out.println("<< Database \"" + dbName + "\" already exists");
							}
						} catch (RemoteException e) {
							System.out.println("<< ERROR - " + e.getMessage());
						}
						break;
					case "insert":
						String dbNameInsert = reader.get(1);
						String value = reader.get(2);
						int intValue = value.isEmpty() ? 0 : Integer.parseInt(value);
						String message = reader.get(3);
						try {
							serveri.insert(dbNameInsert, intValue, message);
							System.out.println("<< DB \"" + dbNameInsert + "\" - record inserted");
						} catch (RemoteException e) {
							System.out.println("<< ERROR - " + e.getMessage());
						}
						break;
					case "update":
						String dbNameUpdate = reader.get(1);
						String valueUpdate = reader.get(2);
						int intValueUpdate = valueUpdate.isEmpty() ? 0 : Integer.parseInt(valueUpdate);
						String messageUpdate = reader.get(3);
						try {
							serveri.update(dbNameUpdate, intValueUpdate, messageUpdate);
							System.out.println("<< DB \"" + dbNameUpdate + "\" - record updated");
						} catch (RemoteException e) {
							System.out.println("<< ERROR - " + e.getMessage());
						}
						break;
					case "get":
						String dbNameGet = reader.get(1);
						String valueGet = reader.get(2);
						int intValueGet = valueGet.isEmpty() ? 0 : Integer.parseInt(valueGet);
						try{
							DBRecord dbRecord = serveri.get(dbNameGet,intValueGet);
							System.out.println("<< DB message: " + dbRecord.getMessage());
						}catch (RemoteException e){
							System.out.println("<< ERROR - " + e.getMessage());
						}
						break;
					case "geta":
						String dbNameGetA = reader.get(1);
						Integer[] idsGetA = new Integer[reader.getColumnCount() - 2];
						for (int i = 2; i < reader.getColumnCount(); i++) {
							String idString = reader.get(i);
							int id = idString.isEmpty() ? 0 : Integer.parseInt(idString);
							idsGetA[i - 2] = id;
						}
						try {
							DBRecord[] recordsGetA = serveri.getA(dbNameGetA, idsGetA);
							for (DBRecord record : recordsGetA) {
								System.out.println("<< DB message: " + record.getMessage());
							}
						} catch (RemoteException e) {
							System.out.println("<< ERROR - " + e.getMessage());
						}
						break;
					case "flush":
						serveri.flush();
						System.out.println("<< Changes flushed to disk :)");
						break;
					default:
						System.out.println("<< ERROR - command \"" + tscreate + "\" not implemented");
						break;
				}
			}
		} catch (Exception e) {
			System.err.println("Data exception: " + e.getMessage());
		}
	}
}
