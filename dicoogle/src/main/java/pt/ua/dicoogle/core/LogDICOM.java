/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.ua.dicoogle.core;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.transform.TransformerConfigurationException;




/**
 *
 * @author Lu??s A. Basti??o Silva <bastiao@ua.pt>
 */
public class LogDICOM
{

    private static LogDICOM instance = null ;

    private static Semaphore sem = new Semaphore(1, true);


    private ArrayList<LogLine> ll = new ArrayList<LogLine>(); 


    private LogDICOM()
    {
        // Nothing to do.
    }


    public static synchronized LogDICOM getInstance()
    {
        try
        {
            sem.acquire();
            if (instance == null)
            {
                instance = new LogDICOM();
            }
            sem.release();
        }
        catch (InterruptedException ex)
        {
            LoggerFactory.getLogger(LogDICOM.class).error(ex.getMessage(), ex);
        }
        return instance;
    }


    public void addLine(LogLine l)
    {
        this.getLl().add(l);
    }

    public void clearLog(){
        this.getLl().clear();
        
        try {
            LogXML log = new LogXML();
            log.printXML();
        } catch (TransformerConfigurationException ex) {
            LoggerFactory.getLogger(LogDICOM.class).error(ex.getMessage(), ex);
        }
    }

    /**
     * @return the ll
     */
    public ArrayList<LogLine> getLl()
    {
        return ll;
    }

    /**
     * @param ll the ll to set
     */
    public void setLl(ArrayList<LogLine> ll)
    {
        this.ll = ll;
    }



}
