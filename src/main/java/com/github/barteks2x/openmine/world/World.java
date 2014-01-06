package com.github.barteks2x.openmine.world;

import com.github.barteks2x.openmine.*;
import com.github.barteks2x.openmine.block.Block;
import com.github.barteks2x.openmine.generator.AChunkGenerator;
import java.util.HashMap;
import java.util.Map;

public class World {
    protected final AChunkGenerator generator;
    protected final long seed;
    protected final Map<ChunkPosition, Chunk> chunks;

    public World(AChunkGenerator generator, long seed) {
        this.generator = generator;
        this.seed = seed;
        this.chunks = new HashMap<ChunkPosition, Chunk>();
    }

    public Chunk getChunkAt(int x, int y, int z) {
        if(!chunkExists(x, y, z)) {
            return Chunk.blankChunk;
        }
        return chunks.get(new ChunkPosition(x, y ,z));
    }
    
    public boolean chunkExists(int x, int y, int z){
        return chunks.containsKey(new ChunkPosition(x, y, z));
    }

    public Chunk getChunkAtBlockCoords(int x, int y, int z) {
        return this.getChunkAt(x >> 4, y >> 4, z >> 4);
    }

    public Block getBlockAt(int x, int y, int z) {
        return this.getChunkAtBlockCoords(x, y, z).getBlockAt(x & 0xf, y & 0xf, z & 0xf);
    }

    public boolean setBlockAt(int x, int y, int z, Block block) {
        this.getChunkAtBlockCoords(x, y, z).setBlockAt(x, y, z, block);
        return true;
    }

    public Chunk loadOrGenerateChunkAt(int x, int y, int z) {
        if(!chunkExists(x, y, z)) {
            setChunkAt(x, y, z, generator.generateChunk(x, y, z));
        }
        return getChunkAt(x, y, z);
    }
    public FloatPosition getSpawnLocation() {
        IntPosition pos = generator.getSpawnPoint();
        return new FloatPosition(pos.getX() + .5F, pos.getY() + 1.6F, pos.getZ() + .5F);
    }

    public void generateChunksNearSpawn() {
        FloatPosition s = this.getSpawnLocation();
        int X = MathHelper.floor(s.getX() / 16F);
        int Y = MathHelper.floor(s.getY() / 16F);
        int Z = MathHelper.floor(s.getZ() / 16F);
        for(int x = -5; x <= 5; ++x){
            for(int y = -5; y <= 5; ++y){
                for(int z = -5; z <= 5; ++z){
                    loadOrGenerateChunkAt(x + X, y + Y, z + Z);
                }
            }
        }
    }

    protected void setChunkAt(int x, int y, int z, Chunk chunk) {
        chunks.put(new ChunkPosition(x, y, z), chunk);
    }
}
