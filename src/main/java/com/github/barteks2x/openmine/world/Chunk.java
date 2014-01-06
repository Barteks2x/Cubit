package com.github.barteks2x.openmine.world;

import com.github.barteks2x.openmine.BlockInChunkPosition;
import com.github.barteks2x.openmine.ChunkPosition;
import com.github.barteks2x.openmine.block.Block;

public class Chunk {

    public static final int CHUNK_X = 16, CHUNK_Y = 16, CHUNK_Z = 16;
    public static final int SIZE = CHUNK_X * CHUNK_Y * CHUNK_Z;

    public static final Chunk blankChunk = new Chunk(0, 0, 0, null) {
        @Override
        public String toString() {
            return "BlankChunk";
        }

        @Override
        public int getX() {
            return 0;
        }

        @Override
        public Chunk setX(int x) {
            return this;
        }

        @Override
        public int getY() {
            return 0;
        }

        @Override
        public Chunk setY(int y) {
            return this;
        }

        @Override
        public int getZ() {
            return 0;
        }

        @Override
        public Chunk setZ(int z) {
            return this;
        }

        @Override
        public Chunk setBlocks(int[] data) {
            return this;
        }

        @Override
        public Chunk setBlockAt(int x, int y, int z, Block b) {
            return this;
        }

        @Override
        public Block getBlockAt(int x, int y, int z) {
            return Block.AIR;
        }

        @Override
        public ChunkPosition getPosition() {
            return new ChunkPosition(0, 0, 0);
        }

        @Override
        public int getBlockCount() {
            return 0;
        }
    };
    private int x;
    private int y;
    private int z;
    private int blockCount;
    private int[] blocks = new int[SIZE];
    private boolean blockCountUpdated;

    public Chunk(int x, int y, int z, int[] blocks) {
        this.blockCount = 0;
        this.blockCountUpdated = false;
        this.x = x;
        this.y = y;
        this.z = z;
        if(blocks == null) {
            blockCountUpdated = true;
            return;
        }
        System.arraycopy(blocks, 0, this.blocks, 0, SIZE);
        this.updateBlockCount();
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Position: (").append(x).append(", ").append(y).
                append(", ").append(z).append(") ").append("Blocks: ").append(blockCount).
                toString();
    }

    public int getX() {
        return x;
    }

    public Chunk setX(int x) {
        this.x = x;
        return this;
    }

    public int getY() {
        return y;
    }

    public Chunk setY(int y) {
        this.y = y;
        return this;
    }

    public int getZ() {
        return z;
    }

    public Chunk setZ(int z) {
        this.z = z;
        return this;
    }

    public Chunk setBlocks(int[] data) {
        if(this.blocks.length != SIZE) {
            this.blocks = new int[SIZE];
        }
        for(int i = 0; i < this.blocks.length; ++i) {
            if(i == data.length) {
                break;
            }
            this.blocks[i] = data[i];
        }
        blockCountUpdated = false;
        return this;
    }

    public Chunk setBlockAt(int x, int y, int z, Block b) {
        blocks[new BlockInChunkPosition(x, y, z).toIndex()] = b.getID();
        return this;
    }

    public Block getBlockAt(int x, int y, int z) {
        return Block.byId(blocks[new BlockInChunkPosition(x, y, z).toIndex()]);
    }

    public ChunkPosition getPosition() {
        return new ChunkPosition(x, y, z);
    }

    public int getBlockCount() {
        if(blockCount < 0 || blockCount > SIZE || !blockCountUpdated) {
            this.updateBlockCount();
        }
        return blockCount;
    }

    private void updateBlockCount() {
        for(int i: blocks) {
            if(Block.byId(i).renderBlock()) {
                ++blockCount;
            }
        }
    }
}
