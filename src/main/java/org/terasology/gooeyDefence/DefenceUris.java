/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.gooeyDefence;

/**
 * Contains a static list of assorted assets used throughout the codebase.
 */
public final class DefenceUris {
    public static final String MONEY_ITEM = "GooeyDefence:Money";
    public static final String PLAYER = "Engine:Player";
    public static final String SHRINE = "GooeyDefence:Shrine";
    public static final String TOWER_ENTITY = "GooeyDefence:TowerEntity";

    public static final String SHRINE_DAMAGED = "GooeyDefence:ShrineDamaged";
    public static final String PATH_EFFECT = "GooeyDefence:PathDisplay";

    public static final String STUN_PARTICLES = "GooeyDefence:StunParticleEffect";
    public static final String ICE_PARTICLES = "GooeyDefence:IceParticleEffect";
    public static final String FIRE_PARTICLES = "GooeyDefence:FireParticleEffect";
    public static final String POISON_PARTICLES = "GooeyDefence:PoisonParticleEffect";

    public static final String SPHERE = "GooeyDefence:Sphere";
    public static final String BULLET = "GooeyDefence:Bullet";

    public static final String DEFENCE_HUD = "GooeyDefence:DefenceHud";
    public static final String CONTROL_SCREEN = "GooeyDefence:ControlScreen";
    public static final String TOWER_SCREEN = "GooeyDefence:TowerInfoScreen";
    public static final String ACTIVATE_SCREEN = "GooeyDefence:ActivateGameScreen";
    public static final String HEALTH_HUD = "Core:HealthHud";
    public static final String LOADING_SCREEN = "Engine:LoadingScreen";
    public static final String DEATH_SCREEN = "Engine:DeathScreen";

    public static final String WAVES_CONFIG = "GooeyDefence:Waves";
    public static final String FIELD_CONFIG = "GooeyDefence:FieldConfig";

    public static final String PLAIN_WORLD_BLOCK = "GooeyDefence:PlainWorldGen";
    public static final String WORLD_BLOCK = "GooeyDefence:WorldBlock";
    public static final String ALT_WORLD_BLOCK = "GooeyDefence:AltWorldBlock";
    public static final String PLAIN_BLOCK = "GooeyDefence:Plain";

    /**
     * Private constructor to prevent creation of instances.
     */
    private DefenceUris() {

    }
}
