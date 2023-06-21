package net.sweenus.simplyskills.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.puffish.skillsmod.SkillsAPI;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellDamageSource;
import net.sweenus.simplyskills.util.HelperMethods;
import net.sweenus.simplyskills.util.SkillReferencePosition;

public class SimplySkillsArrowEntity extends ArrowEntity {
    public SimplySkillsArrowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    private int life;

    @Override
    public void tick() {

        if (this.inGround && this.getOwner() != null) {

            this.age();

            if ((this.getOwner() instanceof ServerPlayerEntity serverPlayer) && SkillsAPI.getUnlockedSkills(serverPlayer,
                    "simplyskills_ranger").get().contains(SkillReferencePosition.rangerSpecialisationArrowRainExplosive)) {
                Box box = HelperMethods.createBox(this, 1);
                for (Entity entities : this.world.getOtherEntities(this, box, EntityPredicates.VALID_LIVING_ENTITY)) {
                    if (entities != null && (this.getOwner() instanceof PlayerEntity player)) {
                        if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {

                            Explosion explosion = this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(),
                                    1.0f, false, Explosion.DestructionType.NONE);
                            this.applyDamageEffects(player, le);

                            if (SkillsAPI.getUnlockedSkills(serverPlayer,
                                    "simplyskills_ranger").get().contains(SkillReferencePosition.rangerSpecialisationArrowRainElemental)) {
                                if (this.random.nextInt(100) < 25)
                                    le.damage(SpellDamageSource.player(MagicSchool.FIRE, player), 5);
                                else if (this.random.nextInt(100) < 45)
                                    le.damage(SpellDamageSource.player(MagicSchool.FROST, player), 5);
                                else if (this.random.nextInt(100) < 65)
                                    le.damage(SpellDamageSource.player(MagicSchool.LIGHTNING, player), 5);
                            }

                            this.discard();

                        }
                    }
                }
            }
        }
        super.tick();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {

        if (this.random.nextInt(100) < 80)
            this.discard();

        super.onBlockHit(blockHitResult);
    }

    protected void age() {
        ++this.life;
        if (this.life >= 600) {
            this.discard();
        }
    }


}
