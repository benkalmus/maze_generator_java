import java.util.Random;

//  ##################################################    CELL    ######################################################
//represents a square on a grid. 
class Cell
{
  public boolean DFS_discovered = false;
  public boolean part_of_maze = false;
  public int set_number;
  private int i;
  private int j;
  private int offset_x = 0;
  private int offset_y = 0;
  //initiates an array holding a boolean that knows if a wall exists on this cell or not
  //        wall = {top, right, bottom, left}
  public boolean[] wall = {true, true, true, true};
  public boolean visited = false;
  private int side;
  public int[] background_rgba = {15, 15 , 230, 100};    //defualt values
  public int[] wall_rgba = {10, 10 , 10, 255};    //defualt values
  
  //constructor
  Cell(int i_coord, int j_coord, int side) 
  {
    this.i = i_coord;
    this.j = j_coord;
    this.visited = false;
    this.side = side;
  }
  void setOffsets(int offset_x_in_pixels, int offset_y_in_pixels)
  {
    this.offset_x = offset_x_in_pixels;
    this.offset_y = offset_y_in_pixels;
  }
  void setBackgroundColour(int r, int g, int b, int a)
  {
    this.background_rgba[0] = r;
    this.background_rgba[1] = g;
    this.background_rgba[2] = b;
    this.background_rgba[3] = a;
  }
  void setWallColour(int r, int g, int b, int a)
  {  
    this.wall_rgba[0] = r;
    this.wall_rgba[1] = g;
    this.wall_rgba[2] = b;
    this.wall_rgba[3] = a;
  }
  void drawBox()      //displays
  {    //calculates the x and y coordinates using its position i,j
    int x = this.i * this.side + this.offset_x;
    int y = this.j * this.side + this.offset_y;
    //if a wall exists on this cell, draw it
    
    if (this.visited)    //if a cell is visited, make it visible on the grid
    {
      noStroke();
      fill(this.background_rgba[0], this.background_rgba[1] , this.background_rgba[2], this.background_rgba[3]);    //purple
      rect(x, y, side, side, 3);     
    }
  }
  void drawWalls()
  {
    stroke(this.wall_rgba[0], this.wall_rgba[1] , this.wall_rgba[2], this.wall_rgba[3]);
    strokeJoin(MITER);
    strokeCap(PROJECT);
    strokeWeight(this.side*0.15);
    int x = this.i * this.side + this.offset_x;
    int y = this.j * this.side + this.offset_y;
    if (this.wall[0])
    {
      line(x, y, x + this.side, y);                //top
    }
    if (this.wall[1])
    {
      line(x+this.side, y, x + this.side, y + side);    //right
    }
    if (this.wall[2])
    {
      line(x, y +this.side, x + this.side, y + this.side);  //bottom
    }
    if (this.wall[3])
    {
      line(x, y, x, y+this.side);                //left
    }
  }
  
  void highlight(int r, int g, int b, int a)    //highlights the cell
  {
    int x = this.i * this.side + this.offset_x;
    int y = this.j * this.side + this.offset_y;
    noStroke();
    fill(r, g, b, a);
    int c = int(this.side*0.1);
    rect(x+c, y+c, side -c*2, this.side-c*2);
    
   
    
  }
  
}