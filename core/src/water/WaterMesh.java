package water;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class WaterMesh extends Mesh {
	
	//Constants
	private final int MAP_SIZE;
	private final float TRIANGLE_WIDTH = 15;
	private final int NUM_TRIANGLES;
	
	
	private float[] vertices;
	private short[] indices;
	private float[] heightMap;
	
	private List<Vector2> waveCenters = new ArrayList<Vector2>();
	
	//wave attributes
	private float MAX_WAVE_HEIGHT = 30;
	private float PERIOD = 130;
	private float SPREADING_RATE = 0.5f;
	
	private final int NUM_WAVES = 1;

	public WaterMesh(int MAP_SIZE) {
		super(true, MAP_SIZE * MAP_SIZE * 3 * 2, (MAP_SIZE-1) * (MAP_SIZE-1) * 6, new VertexAttributes(new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(Usage.Normal,  3, ShaderProgram.NORMAL_ATTRIBUTE)));
		this.NUM_TRIANGLES = (MAP_SIZE-1) * (MAP_SIZE-1) * 2;
		this.MAP_SIZE = MAP_SIZE;
	}
	
	public void init() {
		vertices = new float[MAP_SIZE * MAP_SIZE * 3 * 2]; //*2 for normals
		indices = new short[NUM_TRIANGLES*3]; //3 vertices and 2 tris for quad
		heightMap = new float[MAP_SIZE * MAP_SIZE * 3 * 2];
		initWaveCenters();
		update(0);
	}
	
	private void initWaveCenters() {
		Random rand = new Random();
		int numCenters = 5;
		for(int i = 0; i < numCenters; i++) {
			waveCenters.add(new Vector2(rand.nextInt(MAP_SIZE*2)-MAP_SIZE, rand.nextInt(MAP_SIZE*2)-MAP_SIZE));
		}
	}

	public void update(int time) {
		calculateHeightMap(time);
		positionGridVertices();
		createGridIndices();
		calculateNormals();
		this.setVertices(vertices);
		this.setIndices(indices);
	}
	
	private void calculateHeightMap(int time) {
		final int X_LENGTH = MAP_SIZE;
		final int Y_LENGTH = MAP_SIZE;
		long start;
		long end;
		
		start = System.currentTimeMillis();
		float[][] computedHeightMap = new float[X_LENGTH][Y_LENGTH];
		for(int y = 0; y < Y_LENGTH; y++) {
			for(int x = 0; x < X_LENGTH; x++) {
				computedHeightMap[y][x] = calcHeight(x, y, time);
			}
		}
		end= System.currentTimeMillis();
		System.out.println(end - start);
		int i = 0;
		for(float[] row : computedHeightMap){
			for(float d : row){
				heightMap[i] = d;
				i++;
			}
		}
	}
	
	private float calcHeight(int x, int y, int time) {
		float height = 0;
		float lamda = SPREADING_RATE * PERIOD;
		for(int idxWave = 1; idxWave <= NUM_WAVES; idxWave++) {
			float tempPeriod = PERIOD/idxWave;
			lamda = SPREADING_RATE * tempPeriod;
			for(Vector2 center : waveCenters) {
				float distToCenter = new Vector2(center).dst(x, y);
				height += (float) ((MAX_WAVE_HEIGHT/(idxWave)) * Math.sin(2 * Math.PI * ((time)/tempPeriod - distToCenter/lamda)));
			}
			/*float distToCenter1 = new Vector2(-100, 600).dst(x, y);
			float distToCenter2 = new Vector2(800, -200).dst(x, y);
			float distToCenter3 = new Vector2((float) (3*Math.random()), -0).dst(x, y);
			
			
			height += (float) ((MAX_WAVE_HEIGHT/(idxWave)) * Math.sin(2 * Math.PI * ((time)/tempPeriod - distToCenter1/lamda)));
			height += (float) ((MAX_WAVE_HEIGHT/(idxWave)) * Math.sin(2 * Math.PI * ((time)/tempPeriod - distToCenter2/lamda)));
			height += (float) ((MAX_WAVE_HEIGHT/(idxWave)) * Math.sin(2 * Math.PI * ((time)/tempPeriod - distToCenter3/lamda)));
			height += (float) ((MAX_WAVE_HEIGHT/(idxWave)) * Math.sin(2 * Math.PI * ((time)/tempPeriod - distToCenter1/lamda)));
			height += (float) ((MAX_WAVE_HEIGHT/(idxWave)) * Math.sin(2 * Math.PI * ((time)/tempPeriod - distToCenter2/lamda)));
			height += (float) ((MAX_WAVE_HEIGHT/(idxWave)) * Math.sin(2 * Math.PI * ((time)/tempPeriod - distToCenter3/lamda)));
			height += (float) ((MAX_WAVE_HEIGHT/(idxWave)) * Math.sin(2 * Math.PI * ((time)/tempPeriod - distToCenter1/lamda)));
			height += (float) ((MAX_WAVE_HEIGHT/(idxWave)) * Math.sin(2 * Math.PI * ((time)/tempPeriod - distToCenter2/lamda)));
			height += (float) ((MAX_WAVE_HEIGHT/(idxWave)) * Math.sin(2 * Math.PI * ((time)/tempPeriod - distToCenter3/lamda)));
			height += (float) ((MAX_WAVE_HEIGHT/(idxWave)) * Math.sin(2 * Math.PI * ((time)/tempPeriod - distToCenter1/lamda)));
			height += (float) ((MAX_WAVE_HEIGHT/(idxWave)) * Math.sin(2 * Math.PI * ((time)/tempPeriod - distToCenter2/lamda)));
			height += (float) ((MAX_WAVE_HEIGHT/(idxWave)) * Math.sin(2 * Math.PI * ((time)/tempPeriod - distToCenter3/lamda)));*/
			//height -= (float) ((MAX_WAVE_HEIGHT/(Math.exp(idxWave)/4)) * Math.sin(2 * Math.PI * ((time + Math.random()/2)/PERIOD - x/lamda)));
			//height = (float) (MAX_WAVE_HEIGHT * Math.sin(2 * Math.PI * ((time)/PERIOD - distToCenter/lamda)));
		}
		return height;
	}
	
	private void positionGridVertices() {
		int i = 0;
		int vertex = 0;
		float xStart = -(MAP_SIZE/2*TRIANGLE_WIDTH);
		float yStart = -(MAP_SIZE/2*TRIANGLE_WIDTH);
		for(int row = 0; row< MAP_SIZE; row++) {
			for(int col = 0; col < MAP_SIZE; col++) {
				int coordinate = 0; 
				float x = xStart + TRIANGLE_WIDTH * col;
				float y = yStart + TRIANGLE_WIDTH * row;
				float z = heightMap[vertex];//(float) (waterMap1[vertex] * multiplyer + waterMap2[vertex]);// * (1-multiplyer) + Math.random());				
				vertices[i + coordinate++] = x;
				vertices[i + coordinate++] = y;
				vertices[i + coordinate++] = z;
				//skip for normals
				coordinate += 3;
				i += coordinate;
				vertex++;
			}
		}
	}
	private void createGridIndices() {
		int idxVertex = 0;
		for(int tri = 0; tri < NUM_TRIANGLES*3; tri++) {
			if((idxVertex+1) % MAP_SIZE != 0 || idxVertex == 0) {
				int i = 0;
				
				indices[tri + i++] = (short) (idxVertex+1 + MAP_SIZE);
				indices[tri + i++] = (short) (idxVertex+1 + MAP_SIZE - 1);
				indices[tri + i++] = (short) (idxVertex+1);
						
				indices[tri + i++] = (short) idxVertex;
				indices[tri + i++] = (short) (idxVertex + 1);
				indices[tri + i++] = (short) (idxVertex + MAP_SIZE);
								
				idxVertex++;
				tri+=5;
			}else {
				idxVertex++;
				tri--;
			}
			
		}
	}
	
	private void calculateNormals() {
		//calculate face normals
		Vector3[] faceNormals = new Vector3[NUM_TRIANGLES];
		int face = 0;
		Vector3 orientationPoint = new Vector3(0, 0, 4000);
		for(int row = 0; row < MAP_SIZE-1; row++) {
			for(int col = 0; col < MAP_SIZE-1; col++) { 
				int leftBot = row * MAP_SIZE + col;
				int leftTop = row * MAP_SIZE + col + MAP_SIZE;
				int rightBot = row * MAP_SIZE + col + 1;
				int rightTop = row * MAP_SIZE + col + MAP_SIZE + 1;
				
				Vector3 vec1 = getVertexPosition(leftTop, vertices).sub(getVertexPosition(leftBot, vertices));
				Vector3 vec2 = getVertexPosition(rightBot , vertices).sub(getVertexPosition(leftBot, vertices));
				faceNormals[face] = vec1.crs(vec2);
				faceNormals[face].nor();
				
				if(faceNormals[face].dot(new Vector3(new Vector3(orientationPoint).sub(getVertexPosition(leftBot, vertices)))) < 0) {
					faceNormals[face].scl(-1, -1, -1);
				}
				face++;
				vec1 = getVertexPosition(leftTop, vertices).sub(getVertexPosition(rightTop, vertices));
				vec2 = getVertexPosition(rightBot, vertices).sub(getVertexPosition(rightTop, vertices));
				faceNormals[face] = vec2.crs(vec1);
				faceNormals[face].nor();
				
				if(faceNormals[face].dot(new Vector3(new Vector3(orientationPoint).sub(getVertexPosition(rightTop, vertices)))) < 0) {
					faceNormals[face].scl(-1, -1, -1);
				}
				face++;
			}
		}
		//calculate vertex normals
		Vector3 vertexNormal = new Vector3();
		int innerVert = 0;
		int bottomVert = 0;
		for(int idxPosition = 0; idxPosition < vertices.length/6; idxPosition++) {
			//calculate surrounding faces

			List<Integer> surroundingFaces  = new ArrayList<Integer>();
			//checking edges
			if((float)idxPosition / MAP_SIZE < 1 || idxPosition == 0) { //Bottom edge
				if((float)idxPosition % MAP_SIZE == 0) { //bottom Left corner
					surroundingFaces.add(0);
				}else if(((float)idxPosition+1)% MAP_SIZE == 0) {//bottom right corner
					surroundingFaces.add(idxPosition - 1 + bottomVert);
					surroundingFaces.add(idxPosition + bottomVert);
				}else {
					surroundingFaces.add(idxPosition - 1 + bottomVert);
					surroundingFaces.add(idxPosition + bottomVert);
					surroundingFaces.add(idxPosition + 1 + bottomVert);
					bottomVert++;
				}
			}else if((float)idxPosition % MAP_SIZE == 0 && idxPosition != 0 && MAP_SIZE * MAP_SIZE - idxPosition > MAP_SIZE) { //Left edge
				surroundingFaces.add((idxPosition/MAP_SIZE - 1) * (MAP_SIZE-1)*2);
				surroundingFaces.add((idxPosition/MAP_SIZE - 1) * (MAP_SIZE-1)*2 + 1);
				surroundingFaces.add((idxPosition/MAP_SIZE) * (MAP_SIZE-1)*2);
			}else if(((float)idxPosition+1)% MAP_SIZE == 0 && MAP_SIZE * MAP_SIZE - idxPosition > MAP_SIZE) { //right edge
				surroundingFaces.add(((idxPosition+1)/MAP_SIZE - 1) * (MAP_SIZE-1)*2 - 1);				
				surroundingFaces.add(((idxPosition+1)/MAP_SIZE - 1) * (MAP_SIZE-1)*2 - 1 + (MAP_SIZE-1)*2-1);
				surroundingFaces.add(((idxPosition+1)/MAP_SIZE - 1) * (MAP_SIZE-1)*2 + (MAP_SIZE-1)*2-1);
			}else if(MAP_SIZE * MAP_SIZE - idxPosition <= MAP_SIZE) { //top edge
				if((float)idxPosition % MAP_SIZE == 0) { //Top left corner
					surroundingFaces.add((idxPosition/MAP_SIZE - 1) * (MAP_SIZE-1)*2);
					surroundingFaces.add((idxPosition/MAP_SIZE - 1) * (MAP_SIZE-1)*2 + 1);
				}else if(((float)idxPosition+1)% MAP_SIZE == 0) { //top right corner
					surroundingFaces.add(((idxPosition+1)/MAP_SIZE - 1) * (MAP_SIZE-1)*2 - 1);
				}else {
					//TODO add this INDEZEs
					int row = (int) idxPosition / MAP_SIZE;
					int col = idxPosition - row * MAP_SIZE;
					surroundingFaces.add((row-1) * (MAP_SIZE-1)*2 + col);
					surroundingFaces.add((row-1) * (MAP_SIZE-1)*2 + col + 1);
					surroundingFaces.add((row-1) * (MAP_SIZE-1)*2 + col + 2);
				}
			}else {
				//non edges
				surroundingFaces.add(idxPosition - MAP_SIZE + innerVert);
				surroundingFaces.add(idxPosition - MAP_SIZE + innerVert + 1);
				surroundingFaces.add(idxPosition - MAP_SIZE + innerVert + 2);
				
				surroundingFaces.add(idxPosition - MAP_SIZE - 1 + (MAP_SIZE-1)*2 + innerVert);
				surroundingFaces.add(idxPosition - MAP_SIZE - 1 + (MAP_SIZE-1)*2 + innerVert + 1);
				surroundingFaces.add(idxPosition - MAP_SIZE - 1 + (MAP_SIZE-1)*2 + innerVert + 2);
				innerVert++;
			}
			
			
			for(int idxFace : surroundingFaces) {
				vertexNormal.add(faceNormals[idxFace]);
			}
			vertexNormal.nor();
			
			vertices[idxPosition * 6 + 3] = vertexNormal.x;
			vertices[idxPosition * 6 + 1 + 3] = vertexNormal.y;
			vertices[idxPosition * 6 + 2 + 3] = vertexNormal.z;
		}
	}
	
	private Vector3 getVertexPosition(int index, float[] vertices) {
		float x = vertices[index * 6]; //6 for 3 position and 3 normals
		float y = vertices[index * 6 + 1];
		float z = vertices[index * 6 + 2];
		return new Vector3(x, y, z);
	}

	public void setWaveHeight(float height) {
		this.MAX_WAVE_HEIGHT = height;
	}
	
	public void setPeriod(float period) {
		this.PERIOD = period;
		System.out.println(period);
	}
	public void setSpreadingrate(float rate) {
		this.SPREADING_RATE = rate;
	}
}
