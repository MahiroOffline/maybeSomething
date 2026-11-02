package com.haruno.bia2.math;

public class Box {
    public static final Anchor ORIGIN = Anchor.TOP_LEFT;

    public Position origin = null;
    public double x;
    public double y;
    public double z;

    public Box(double x,double y,double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Box(double x,double y){
        this(x,y,0.0);
    }

    public Box(double x){
        this(x,0.0,0.0);;
    }

    public void setOrigin(Position origin){
        this.origin = origin;
    }

    public void initOrigin(Position position,Anchor anchor){
        this.origin = anchorToPosition(x,y,z,position,anchor,ORIGIN);
    }

    public Position anchorToPosition(Anchor anchor){
        if(origin == null)
            throw new NullPointerException("This box's origin position has not been initialized");

        return anchorToPosition(x,y,z,origin,ORIGIN,anchor);
    }

    public Anchor positionToAnchor(Position position){
        if(origin == null)
            throw new NullPointerException("This box's origin position has not been initialized");

        double dx = position.x;
        double dy = position.y;
        double dz = position.z;

        if(x == 0)
            dx = origin.x;
        if(y == 0)
            dy = origin.y;
        if(z == 0)
            dz = origin.z;

        Position operatedPosition = new Position(dx,dy,dz);
        return positionToAnchor(x,y,z,origin,ORIGIN,operatedPosition);
    }

    private static Position anchorToPosition(double x,double y,double z,Position position,Anchor local,Anchor target){
        double relativeX = position.x + (target.x - local.x) * x;
        double relativeY = position.y + (target.y - local.y) * y;
        double relativeZ = position.z + (target.z - local.z) * z;

        Position offset = new Position(relativeX,relativeY,relativeZ);
        return Position.add(position,offset);
    }

    private static Anchor positionToAnchor(double x,double y,double z,Position local,Anchor anchor,Position target){
        double relativeX = (target.x - local.x) == 0 ? 0 : (target.x - local.x) / x;
        double relativeY = (target.y - local.y) == 0 ? 0 : (target.y - local.y) / y;
        double relativeZ = (target.z - local.z) == 0 ? 0 : (target.z - local.z) / z;

        Anchor offset = new Anchor(relativeX,relativeY,relativeZ);
        return Anchor.add(anchor,offset);
    }
}
