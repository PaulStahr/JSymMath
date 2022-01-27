package util;

public class Interpolator {
    public static float interpolateUnsignedPoint(double xf, double yf, double zf, int data[], int width, int height, int depth)
    {
        int x = (int)(xf * 0x100);
        int y = (int)(yf * 0x100);
        int z = (int)(zf * 0x100);
        int xMod = x % 0x100;
        int yMod = y % 0x100;
        int zMod = z % 0x100;
        x /= 0x100;
        y /= 0x100;
        z /= 0x100;
        if (x + 1 == width)   {--x;xMod = 255;}
        if (y + 1 == height)  {--y;yMod = 255;}
        if (z + 1 == depth)   {--z;zMod = 255;}
        int index = ((z * height) + y) * width + x;
        float result = 0;
        int xModI = 0x100 - xMod;
        int yModI = 0x100 - yMod;
        int zModI = 0x100 - zMod;
        int xModD = 2 * xMod - 0x100;
        int yModD = 2 * yMod - 0x100;
        int zModD = 2 * zMod - 0x100;
        for (int i = 0; i < 8; ++i)
        {
            int m0 =  i % 2;
            int m1 = (i / 2) % 2;
            int m2 = (i / 4) % 2;
            long multiply  = (xModI + m0 * xModD) * (yModI + m1 * yModD) * (zModI + m2 * zModD);
            float dat = data[index + ((m2 * height) + m1) * width + m0];
            if (dat < 0) {dat -= Integer.MIN_VALUE; dat += Integer.MAX_VALUE;}
            result += dat * multiply;
        }
        return result / 0x1000000;
    }

    public static float interpolatePoint(double xf, double yf, double zf, float data[], int width, int height, int depth)
    {
        int x = (int)(xf * 0x100);
        int y = (int)(yf * 0x100);
        int z = (int)(zf * 0x100);
        int xMod = x % 0x100;
        int yMod = y % 0x100;
        int zMod = z % 0x100;
        x /= 0x100;
        y /= 0x100;
        z /= 0x100;
        if (x + 1 == width)   {--x;xMod = 255;}
        if (y + 1 == height)  {--y;yMod = 255;}
        if (z + 1 == depth)   {--z;zMod = 255;}
        int index = ((z * height) + y) * width + x;
        float result = 0;
        int xModI = 0x100 - xMod;
        int yModI = 0x100 - yMod;
        int zModI = 0x100 - zMod;
        int xModD = 2 * xMod - 0x100;
        int yModD = 2 * yMod - 0x100;
        int zModD = 2 * zMod - 0x100;

        for (int i = 0; i < 8; ++i)
        {
            int m0 =  i % 2;
            int m1 = (i / 2) % 2;
            int m2 = (i / 4) % 2;
            long multiply  = (xModI + m0 * xModD) * (yModI + m1 * yModD) * (zModI + m2 * zModD);
            float dat = data[index + ((m2 * height) + m1) * width + m0];
            result += dat * multiply;
        }
        return result / 0x1000000;
    }
}
