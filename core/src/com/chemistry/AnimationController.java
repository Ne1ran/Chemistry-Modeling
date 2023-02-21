package com.chemistry;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.chemistry.dto.Equipment;
import com.chemistry.dto.Substance;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.chemistry.ExperimentWindow.*;
import static com.chemistry.ReactionHandler.substancesColors;
import static com.chemistry.ReactionHandler.substancesEffects;

public class AnimationController {

    public Map<String, Array<String>> effectsMap;

    Vector2 startPos;
    Vector2 currentPos;
    Vector2 endPos;
    Float speed;
    int directionX;
    int directionY;
    float rotation = 5;
    float endRotation = 135;
    float startRotation;
    Sprite textureSprite;
    boolean reachedX = false;
    boolean reachedY = false;
    static boolean startColorChange = false;

    Equipment currentlyUsedEquip;


    public AnimationController(Vector2 startPos, Vector2 endPos, Sprite sprite, Equipment equipment) {
        fillEffectMap();

        this.startPos = startPos;
        this.endPos = endPos;
        this.currentPos = startPos;
        this.textureSprite = sprite;
        this.startRotation = sprite.getRotation();
        this.currentlyUsedEquip = equipment;
        System.out.println(startRotation + "start rot");
        animationStarted = true;
    }

    public void CalculateSpeed(){
        Double x = (double) startPos.x - endPos.x;
        Double y = (double) startPos.y - endPos.y;
        float length;
        length = (float) Math.sqrt(x * x + y * y);
        speed = length / 100;
    }

    public void CalculateDirection(){
        if (startPos.x > endPos.x){
            directionX = -1;
        } else if (startPos.x < endPos.x){
            directionX = 1;
        } else directionX = 0;

        if (startPos.y > endPos.y){
            directionY = 1;
        } else if (startPos.y < endPos.y) {
            directionY = -1;
        } else directionY = 0;
    }

    public Vector2 Move () throws SQLException, ClassNotFoundException {

        if (!reachedX) {
            if (directionX == 1) {
                if (currentPos.x < endPos.x) {
                    currentPos.x += speed;
                } else {
                    reachedX = true;
                }
            } else if (directionX == -1) {
                if (currentPos.x > endPos.x) {
                    currentPos.x -= speed;
                } else {
                    reachedX = true;
                }
            } else {
                //how?
            }
        }

        if (!reachedY) {
            if (directionY == 1) {
                if (currentPos.y > endPos.y) {
                    currentPos.y -= speed;
                } else {
                    reachedY = true;
                }
            } else if (directionY == -1) {
                if (currentPos.y < endPos.y) {
                    currentPos.y += speed;
                } else {
                    reachedY = true;
                }
            } else {
                //how?
            }
        }

        if (reachedX && reachedY){
            PlaySound("addSubstance");
            StartRotation();
        }

        return new Vector2(startPos.x, startPos.y);
    }

    public void StartEffectAnimation(String effect){
        for (String string : effectsMap.keySet()){
            if (string.equals(effect)){
                System.out.println("EFFECT STARTO");
            }
        }
    }

    public void StartRotation() throws SQLException, ClassNotFoundException {
        float currentRotation = animationTexture.getRotation();
        if (!(currentRotation > endRotation)){
            currentRotation += rotation;
            if (currentRotation>360 || currentRotation<-360){
                currentRotation = 0f;
            }
            animationTexture.setRotation(currentRotation);
        } else {
            StopAnimation();
        }
    }

    public void StopAnimation() throws SQLException, ClassNotFoundException {
        reachedX = false;
        reachedY = false;
        animationStarted = false;
        currentPos = startPos;
        animatedSubstance = new Substance();
        isSomethingBeingAnimated = false;
        StartColorChangingInEquipment(currentlyUsedEquip);
        StartEffectPlaying(currentlyUsedEquip);
    }

    public void fillEffectMap(){
        effectsMap = new HashMap<>();
        effectsMap.put("explosion", new Array<>("1,2,3,4,5".split(",")));
        effectsMap.put("fire", new Array<>("1,2,3,4,5".split(",")));
    }

    public AnimationController(){

    }

    public void colorChangeInEquipment(Equipment equipment) throws SQLException, ClassNotFoundException {
        String equipmentTexturePath = handler.getEquipmentTexturePathById(equipment.getId());

        String colors = String.join(" ", substancesColors).replaceAll("0", "").trim();
        String pickedColor = "";
        String[] colorsArray = colors.split(" ");
        if (colorsArray.length == 0){
            //no color
        } else if (colorsArray.length == 1){
            pickedColor = colorsArray[0] + "_";
            //we have it
        } else {
            //roll random
        }

        if (!pickedColor.equals("_")) {
            equipmentTexturePath = "Textures/" + pickedColor + equipmentTexturePath;
            equipment.setTexture_path(new Texture(equipmentTexturePath));
            substancesColors = new Array<>();
        }
    }

    public void startEffect() {
        String effects = String.join(" ", substancesEffects).replaceAll("0", "").trim();

        String pickedEffect = "";
        String[] effectsArray = effects.split(" ");
        if (effectsArray.length == 0){
            //no effects
        } else if (effectsArray.length == 1){
            pickedEffect = effectsArray[0];
        } else {
            //check for speciality
        }
        System.out.println("ima here");
        PlayEffectSound(pickedEffect);
        substancesEffects = new Array<>();
    }
}
