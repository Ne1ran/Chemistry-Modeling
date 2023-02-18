package com.chemistry;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.chemistry.dto.Substance;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.chemistry.ExperimentWindow.*;

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

    public AnimationController(Vector2 startPos, Vector2 endPos, Sprite sprite) {
        fillEffectMap();

        this.startPos = startPos;
        this.endPos = endPos;
        this.currentPos = startPos;
        this.textureSprite = sprite;
        this.startRotation = sprite.getRotation();
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
        StartColorChangingInEquipment();
    }

    public void fillEffectMap(){
        effectsMap = new HashMap<>();
        effectsMap.put("explosion", new Array<>("1,2,3,4,5".split(",")));
        effectsMap.put("fire", new Array<>("1,2,3,4,5".split(",")));
    }

    public AnimationController(){

    }
}
