import java.util.Stack;
import java.util.Random;
import controlP5.*;

//GUI
ControlP5 cp5;
DropdownList d1;
DropdownList mix;
Slider slider_width;
Slider slider_side;
Slider slider_r;
Slider slider_g;
Slider slider_b;
Slider slider_wr;
Slider slider_wg;
Slider slider_wb;
Slider slider_fr;
Slider slider_fg;
Slider slider_fb;
Slider slider_split;

Textlabel start_lbl;
Textlabel end_lbl;

int maze_selected;
boolean new_maze = false;
boolean set_start_cell = false;
boolean build_maze = false;
int cell_i = -1;
int cell_j = -1;
int start_gen_i = -1;
int start_gen_j = -1;
int dfs_start_i = 0;
int dfs_start_j = 0;
int dfs_end_i = 0;
int dfs_end_j = 0;
boolean run_dfs = false;

int dfs_counter = 0;
int cells = 0;
boolean dfs_found = false;
//benchmarks
long benchmark;
Instant end; Instant start;
Instant k_end; Instant k_start;

ArrayList<Cell> solutionCells = new ArrayList<Cell>();
ArrayList<Cell> DFScells = new ArrayList<Cell>();

Growing_Tree            gmaze = new Growing_Tree();
Recursive_Backtracker   rmaze = new Recursive_Backtracker();
Kruskals_Maze           kmaze = new Kruskals_Maze();

  
//  ##################################################      SETUP      #######################################
void setup()
{
  size(1100, 900);
  frameRate(120);
  //Cell root = kmaze.cell_object[0][0];
  //DFS(root, rmaze.cell_object[rmaze.columns - 1][rmaze.columns - 1]);
    
     
  cp5 = new ControlP5(this);
  Textlabel lbl1 = cp5.addTextlabel("label1")
                    .setText("Select a maze generation algorithm")
                    .setPosition(10,20);
                    
  slider_width = cp5.addSlider("Size of Maze",         100, 800, 600, 10, 60, 120, 20);
  slider_side = cp5.addSlider("Side length",           1, 50, 15, 10, 100, 120, 20);
  
  Textlabel lbl2 = cp5.addTextlabel("label2")
                    .setText("Select Background colour")
                    .setPosition(10, 135);
  slider_r = cp5.addSlider("\n\n R",           0, 255, 70, 10, 150, 40, 20);
  slider_g = cp5.addSlider("\n\n G",           0, 255, 25, 60, 150, 40, 20);
  slider_b = cp5.addSlider("\n\n B",           0, 255, 170, 110, 150, 40, 20);
  
  Textlabel lbl3 = cp5.addTextlabel("label3")
                    .setText("Select Gradient colour")
                    .setPosition(10, 190);
  slider_fr = cp5.addSlider("\n\n Gradient R",           0, 255, 75, 10, 200, 40, 20);
  slider_fg = cp5.addSlider("\n\n Gradient G",           0, 255, 170, 60, 200, 40, 20);
  slider_fb = cp5.addSlider("\n\n Gradient B",           0, 255, 200, 110, 200, 40, 20);
  
                    
  slider_wr = cp5.addSlider("\n\n Wall R",           0, 255, 10, 10, 250, 40, 20);
  slider_wg = cp5.addSlider("\n\n Wall G",           0, 255, 10, 60, 250, 40, 20);
  slider_wb = cp5.addSlider("\n\n Wall B",           0, 255, 10, 110, 250, 40, 20);
        
  Textlabel lbl4 = cp5.addTextlabel("label4")
                    .setText("Additional Options for Growing Tree Mazes")
                    .setPosition(10, 300);
                    
                    
  slider_split = cp5.addSlider("Split Percent",           0, 100, 5, 10, 340, 120, 20);
                    
  Textlabel lbl5 = cp5.addTextlabel("label5")
                    .setText("Depth First Search Solver")
                    .setPosition(10, 470);
                    
  start_lbl = cp5.addTextlabel("start_lbl")
                .setText("Start coords: 0, 0")
                .setPosition(10, 500);
  end_lbl = cp5.addTextlabel("end_lbl")
                .setText("End coords: 0, 0")
                .setPosition(10, 520);
 //************************************************       
 
  cp5.addButton("Set_Begin").setValue(0).setPosition(110, 490).setSize(80, 20);
  cp5.addButton("Set_End").setValue(0).setPosition(110, 515).setSize(80, 20);
  cp5.addButton("Run_DFS").setValue(0).setPosition(40, 550).setSize(120, 20);  
  
  cp5.addButton("Animate").setValue(0).setPosition(20, 380).setSize(60, 20);
  cp5.addButton("Stop").setValue(0).setPosition(100, 380).setSize(60, 20);
  cp5.addButton("Start_Cell").setValue(0).setPosition(20, 410).setSize(100, 30);
  cp5.addButton("Render").setValue(0).setPosition(20, 600).setSize(160, 60);
  
  d1 = cp5.addDropdownList("Select an algorithm").setPosition(10, 40).setSize(150, 500);
  d1.addItem("Growing Tree Algorithm", 0).setItemHeight(30);
  d1.addItem("Recursive Backtracker", 1).setItemHeight(30);
  d1.addItem("Kruskal's", 2).setItemHeight(30); 
  
  mix = cp5.addDropdownList("Select a mix type").setPosition(10, 320).setSize(150, 500);
  mix.addItem("Always Random", 0).setItemHeight(30);
  mix.addItem("Old to New Cell Mix", 1).setItemHeight(30);
  mix.addItem("Old to Random Mix", 2).setItemHeight(30);
  mix.addItem("New to Random Mix", 3).setItemHeight(30);
  
  
}

//  ################################################      DRAW       ########################################################
boolean init = false;
void draw()
{
  background(60);
  strokeWeight(2);
  stroke(255, 255, 255, 255);
  line(200, 0, 200, height);
  line(0, 290, 200, 290);
  line(0, 580, 200, 580);
  stroke(255, 255, 255, 50);
  line(0, 460, 200, 460);
  maze_selected = int(d1.getValue());
  int r = int(slider_r.getValue());
  int g = int(slider_g.getValue());
  int b = int(slider_b.getValue());
  int fr = int(slider_fr.getValue());
  int fg = int(slider_fg.getValue());
  int fb = int(slider_fb.getValue());
  //tree
  if (maze_selected == 0)
  {
    if (new_maze)
    {
      dfs_counter = 0;
      solutionCells.clear();
      DFScells.clear();
      gmaze = null;
      rmaze = null;
      kmaze = null;  
      gmaze = new Growing_Tree();
      gmaze.begin(int(slider_width.getValue()), int(slider_side.getValue()));
      gmaze.setOffset(250, 50);
      gmaze.setWallColour(int(slider_wr.getValue()), int(slider_wg.getValue()), int(slider_wb.getValue()), 255);
      gmaze.setColourGradient(r, g, b, fr, fg, fb);
      gmaze.setMix(int( mix.getValue()));
      gmaze.setSplitPercentage(int(slider_split.getValue()));
      gmaze.setStartCell(start_gen_i, start_gen_j);
      gmaze.generate_maze();
      new_maze = false;
    }
    else if (build_maze)
    {
      if (init == false)
      {      
        dfs_counter = 0;
        solutionCells.clear();
        DFScells.clear();
        gmaze = null;
        rmaze = null;
        kmaze = null;
        gmaze = new Growing_Tree();
        gmaze.begin(int(slider_width.getValue()), int(slider_side.getValue()));
        gmaze.setOffset(250, 50);
        gmaze.setWallColour(int(slider_wr.getValue()), int(slider_wg.getValue()), int(slider_wb.getValue()), 255);
        gmaze.setColourGradient(r, g, b, fr, fg, fb);
        gmaze.setMix(int( mix.getValue()));
        gmaze.setSplitPercentage(int(slider_split.getValue()));
        gmaze.setStartCell(start_gen_i, start_gen_j);
        init = true;
      }
      if (gmaze.build_animation())
      {
        build_maze = false;
        init = false;
      }
    }
    else
    {
      if (run_dfs == true)
      {
        gmaze.setAllUndiscovered();
        dfs_counter = 0;
        cells = 0;
        dfs_found = false;
        solutionCells.clear();
        DFScells.clear();
        //runs the Depth first search algorithm
        if (dfs_end_i < gmaze.columns && dfs_end_j < gmaze.rows)
        {
          DFS(gmaze.cell_object[dfs_start_i][dfs_start_j], gmaze.cell_object[dfs_end_i][dfs_end_j]);
        }
        run_dfs = false;
      }
      gmaze.update();
      displayDFS();
    }
  }
  //recursive
  else if (maze_selected == 1)
  {
    if (new_maze)
    {
      gmaze = null;
      rmaze = null;
      kmaze = null;
      rmaze = new Recursive_Backtracker();
      rmaze.begin(int(slider_width.getValue()), int(slider_side.getValue()));
      rmaze.setOffset(250, 50);
      rmaze.setWallColour(int(slider_wr.getValue()), int(slider_wg.getValue()), int(slider_wb.getValue()), 255);
      rmaze.setColourGradient(r, g, b, fr, fg, fb);
      
      rmaze.generate_maze();
      new_maze = false;
    }
    if (rmaze != null)
    {
      rmaze.update();
    }
  }
  //kruskals
  else if (maze_selected == 2)
  {
    if (new_maze)
    {
      gmaze = null;
      rmaze = null;
      kmaze = null;
      kmaze = new Kruskals_Maze(); 
      kmaze.begin(int(slider_width.getValue()), int(slider_side.getValue()));
      kmaze.setOffset(250, 50);
      kmaze.setWallColour(int(slider_wr.getValue()), int(slider_wg.getValue()), int(slider_wb.getValue()), 255);
      kmaze.setColourGradient(r, g, b, fr, fg, fb);
      
      kmaze.generate_maze();
      new_maze = false;
    }
    if(kmaze != null)
    {
      kmaze.update();
    }
  }
}

void mouseClicked() 
{
  //which cell was clicked on
  cell_i = (mouseX - 250)/int(slider_side.getValue());
  cell_j = (mouseY - 50)/int(slider_side.getValue());
  if (gmaze != null)
  {
    if (cell_j >= gmaze.rows || cell_i >= gmaze.columns)
    {
      cell_i = cell_j = -1;
    }
  }
  
  System.out.println("i = " +cell_i + " j = " + cell_j + " st " + set_start_cell);
  if (set_start_cell == true)
  {
    start_gen_i = cell_i;
    start_gen_j = cell_j;
    set_start_cell = false;
  }
}

public void Set_Begin(int value) {
  if (cell_i >= 0 && cell_j >= 0)
  {
    dfs_start_i = cell_i;
    dfs_start_j = cell_j;
    start_lbl.setText("Start coords: "+dfs_start_i + ", " + dfs_start_j);
  }
}

public void Set_End(int value) {
  if (cell_i >= 0 && cell_j >= 0)
  {
    dfs_end_i = cell_i;
    dfs_end_j = cell_j;
    end_lbl.setText("End coords: "+dfs_end_i + ", " + dfs_end_j);
  }
}

public void Run_DFS(int value) {
  if (frameCount > 60)
  {
    run_dfs = true;
  }
}

public void Render(int value) {
  if (frameCount > 60){
    new_maze = true;
  }
}
public void Animate(int value) {
  if (frameCount > 60){
    build_maze = true;
  }
  set_start_cell = false;
}
public void Stop(int value) {
  if (frameCount > 60){
    build_maze = false;
    init = false;
  }
  set_start_cell = false;
}
public void Start_Cell(int value) {
  if (frameCount > 60){
    start_gen_i = cell_i;
    start_gen_j = cell_j;
    set_start_cell = false;
  }
  System.out.println("prev " +start_gen_i + " prev j " + start_gen_j + " and " + set_start_cell);
}

public void controlEvent(ControlEvent event) 
{
}


void displayDFS()
{
  if (dfs_counter < DFScells.size())dfs_counter++;
  for (int i = 0; i < dfs_counter; i++)
  {
    DFScells.get(i).highlight(10, 220, 30, 150);
    
    if (dfs_counter >= DFScells.size())
    {  
      for (int j = 0; j < solutionCells.size(); j++)
      {
        solutionCells.get( j).highlight(150, 20, 30, 30);
      }
    }
  }
}

boolean DFS(Cell node, Cell end)
{
  
  node.DFS_discovered = true;
  //node.highlight(0,255,0, 200);
  cells++;
  //wall = {top, right, bottom, left}
  if (node.i == end.i && node.j == end.j)
  {
    System.out.println("Found end " + cells);
    solutionCells.add(node);
    dfs_found = true;
    return true;
  }
  if (dfs_found == false)
  {
    DFScells.add(node);
    //for all edges
    if (node.wall[1] == false && node.i+1 <= gmaze.columns -1) 
    {
      if (gmaze.cell_object[node.i+1][node.j].DFS_discovered == false)
      {
        DFS(gmaze.cell_object[node.i+1][node.j], end);
      }
    }
    if (node.wall[2] == false && node.j +1 <= gmaze.rows -1)
    {
      if (gmaze.cell_object[node.i][node.j+1].DFS_discovered == false)
      {
        DFS(gmaze.cell_object[node.i][node.j+1], end);
      }
    }
    if (node.wall[3] == false && node.i -1 >= 0)
    {
      if (gmaze.cell_object[node.i-1][node.j].DFS_discovered == false)
      {
        DFS(gmaze.cell_object[node.i-1][node.j], end);
      }
    }
    if (node.wall[0] == false && node.j-1 >= 0)
    {
      if (gmaze.cell_object[node.i][node.j-1].DFS_discovered == false)
      {
        DFS(gmaze.cell_object[node.i][node.j-1], end);
      }
    }
    if (dfs_found == true)   solutionCells.add(node);
    //if (discoveredNeighbour == false)   solutionCells.remove(node);
    
  }
  //else solutionCells.add(node);
  return false;
}



/*
void keyPressed() 
{
  int keyIndex = -1;
  if (key >= 'A' && key <= 'Z') {
    keyIndex = key - 'A';
  } else if (key >= 'a' && key <= 'z') {
    keyIndex = key - 'a';
  }
  if (keyIndex == -1) 
  {
    // If it's not a letter key, clear the screen
    //background(0);
  } 
  else { 
    // It's a letter key, fill a rectangle
    
  }
}
*/