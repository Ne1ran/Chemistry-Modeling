package com.chemistry;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DBHandlerTest {
    private final DBHandler handler = new DBHandler();

    @Test
    public void getConnection() {
        Assert.assertNotNull(handler.getConnection());
    }

    @Test
    public void getUsingSubstancesIDs() {
        try {
            Assert.assertNotNull(handler.getUsingSubstancesIDs("1"));
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getSubstanceByID() {
        try {
            Assert.assertNotNull(handler.getSubstanceByID("1"));
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getUsingEquipmentIDs() {
        try {
            Assert.assertNotNull(handler.getUsingEquipmentIDs("1"));
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getEquipmentByID() {
        try {
            Assert.assertNotNull(handler.getEquipmentByID("1"));
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getFoundationByName() {
        try {
            Assert.assertNotNull(handler.getFoundationByName("Na"));
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getOxidByName() {
        try {
            Assert.assertNotNull(handler.getOxidByName("Cl"));
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getAllSubstances() {
        try {
            Assert.assertNotNull(handler.getAllSubstances());
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getSubstanceByIDInSubsExpsTable() {
        try {
            Assert.assertNotNull(handler.getSubstanceByIDInSubsExpsTable("1"));
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getSubstanceByIDInSubsExpsTableForExpWindow() {
        try {
            Assert.assertNotNull(handler.getSubstanceByIDInSubsExpsTableForExpWindow("1", "1"));
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getEquipmentByIDInEquipExpTable() {
        try {
            Assert.assertNotNull(handler.getEquipmentByIDInEquipExpTable("1"));
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getEquipmentByIDInEquipExpTableForExpWindow() {
        try {
            Assert.assertNotNull(handler.getEquipmentByIDInEquipExpTableForExpWindow("1", "1"));
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getAllEquipmentsNames() {
        try {
            Assert.assertNotNull(handler.getAllEquipmentsNames());
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getAllSystemExperiments() {
        try {
            Assert.assertNotNull(handler.getAllSystemExperiments());
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getAllCustomExperiments() {
        try {
            Assert.assertNotNull(handler.getAllCustomExperiments("Соусов Илья Владимирович"));
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}