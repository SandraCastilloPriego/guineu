/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.database.intro;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author scsandra
 */
public class Inmysql extends InOracle implements InDataBase {

    @Override
    public Connection connect() {
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/lcmsgcgctof",
                    "root", "");

            /* con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306?autoReconnect=true/lcmsgcgctof", "root", ""); 
            System.out.println("conectado");*/
            return con;
        } catch (Exception e) {
            System.out.println(e + "No se puede conectar a mysql");
            return null;
        }

    }
}
