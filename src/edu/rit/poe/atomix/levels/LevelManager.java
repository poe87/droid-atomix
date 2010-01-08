/*
 * LevelManager.java
 *
 * Version:
 *      $Id$
 *
 * Copyright (c) 2009 Peter O. Erickson
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package edu.rit.poe.atomix.levels;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author  Peter O. Erickson
 *
 * @version $Id$
 */
public class LevelManager {
    
    public static final int FIRST_LEVEL = 1;
    
    public static final String ID = "_id";
    
    public static final String LEVEL_DESCRIPTION = "level";
    
    public static final String LEVEL_NAME = "name";
    
    public static final String LEVELS_DIRECTORY = "levels";
    
    private static volatile LevelManager instance;
    
    private Map<Integer, Level> levelMap;
    
    /**
     * Constructs a new <tt>LevelManager</tt>.
     */
    private LevelManager() {
    }
    
    public static final LevelManager getInstance() {
        // DCL anti-pattern avoided by way of 'volatile'
        if ( instance == null ) {
            synchronized( LevelManager.class ) {
                if ( instance == null ) {
                    instance = new LevelManager();
                }
            }
        }
        return instance;
    }
    
    public void init( Context context ) {
        levelMap = new HashMap<Integer, Level>();
        
        Level level = null;
        try {
            AssetManager am = context.getAssets();
            
            // get all level files in the "levels" directory
            String[] levelFiles = am.list( LEVELS_DIRECTORY );
            
            Log.d( "LevelManager", "Level File List:" );
            for ( String levelFile : levelFiles ) {
                Log.d( "LevelManager FILE:", levelFile );
                
                InputStream is = am.open( LEVELS_DIRECTORY + File.separator
                        + levelFile );
                level = Level.loadLevel( is );
                
                levelMap.put( level.getLevel(), level );
            }
        } catch ( Exception e ) {
            // ignore
        }
    }
    
    public Level getLevel( int levelNumber ) {
        return levelMap.get( levelNumber );
    }
    
    public boolean hasLevel( int levelNumber ) {
        return levelMap.containsKey( levelNumber );
    }
    
    /**
     * Returns a <tt>Cursor</tt> of the loaded levels' number and formula.
     * 
     * @return  a <tt>Cursor</tt> of levels sorted in ascending order
     */
    public Cursor getLevels() {
        MatrixCursor cursor =
                new MatrixCursor( new String[] { ID, LEVEL_DESCRIPTION,
                LEVEL_NAME } );
        
        // ensure the cursor is sorted in ascending order
        List<Integer> levelList = new ArrayList<Integer>( levelMap.keySet() );
        Collections.sort( levelList );
        for ( Integer level : levelList ) {
            Object[] row = new Object[ 3 ];
            row[ 0 ] = level;
            row[ 1 ] = "Level " + level;
            row[ 2 ] = levelMap.get( level ).getName();
            
            cursor.addRow( row );
        }
        
        return cursor;
    }
    
} // LevelManager
