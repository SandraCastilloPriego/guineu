/*
 * Copyright 2007-2010 VTT Biotechnology
 * This file is part of Guineu.
 *
 * Guineu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Guineu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Guineu; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package guineu.database.intro.impl;

import guineu.database.intro.*;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * MySQL connection
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
