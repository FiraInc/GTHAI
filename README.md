# GoodToHave AI (GTHAI)
GTHAI is an AI library that lets you easily create AI software. 
You can give it whatever you want as an input, and it will automatically figure out what to do with it,
to give you your wanted output. You can customize the amount of neurons, the amount of layers, set a limit of iterations etc.

This library is part of the GoodToHave pack. It is independent from the other Libraries, but go check them out for more useful libraries

## Installation
The library is easily installed by following these steps:
* Create a java project.
* Create a folder named 'lib' next to your 'src' folder.
* Put the GTHAI.jar file in the 'lib' folder.

####Follow the steps for your editing program

##### IntelliJ
* Open your **project structure**, and select **'Libraries'**.
* Then press the **'+'** sign at the top of the **project structure** window, and select **'Java'**.
* Navigate to the **'lib'** folder where you put the **'GTHAI.jar'** file.
* Select the **'GTHAI.jar'** file
* Click **OK**.
* Click **OK** again.
* Click **Apply**.
* Click **OK**.

##### Eclipse
* In Eclipse, in the **Package Explorer** pane, right-click your project.
* Click **Properties**.
* In the left pane, click **Java Build Path**.
* On the **Libraries** tab, click **Add External JARs**.
* Navigate to the **'lib'** folder where you put the **'GTHAI.jar'** file
* Select the **'GTHAI.jar'** file
* Click **Open**.
* Click **OK**.

## Usage
### How it works
There are two classes that will be used a lot. **'NeuralNetworkManager.class'** and **'TrainingDataEncoder.class'**.
You do not have to use the TrainingDataEncoder, but it can help you transform your dataset to data that the AI can read.

### Preparing training data
#### Images
If your trainingdata consists of images, the **'TrainingDataEncoder.class'** class will help you a lot.
The following code will get all the images in folder *'C:/myuser/documents/ai/dataA'* and transform
them into an array of double *(double[])* that the AI is able to take as input.

>Note: The images must be of same width and height. They also need .jpg or .png as extension
```java
import com.zostio.myai.training.TrainingData;
import com.zostio.myai.training.TrainingDataEncoder;

public class MyApplication {

    ArrayList<TrainingData> list;

    public void main (String[] args) {
        TrainingDataEncoder encoder = new TrainingDataEncoder();
        encoder.setImageResize(100);
        list = encoder.getTrainingDataset("C:/myuser/documents/ai/dataA", 
            new double[]{0,1});
    }
}
```
If the image have a large resolution, you can speed up the training process of the AI by adding
using the **'setImageResize(int width)'** method of the **'TrainingDataEncoder.class'**

##### Example:

    encoder.setImageResize(100);


### Creating a neural network
#### How to use the AI
For the AI to work it needs a dataset. Then the AI will be trained based on the dataset.
After the preferred amount of training is done, the AI is ready to be used. 

In practice you start by defining the **'NeuralNetworkManager.class'**. 
You must provide the manager information about how many neurons and hidden layers you want it to use,
information about how many outcomes there are, and optionally limit the training to a certain 
amount of iterations.



    NeuralNetworkManager manager = new NeuralNetworkManager(new int[]{16,16}, 2, 5000);

>In this example we used 2 hidden layers, with 16 neurons each. We also told the AI there are
>2 possible outcomes, and limited the iterations to 5000

##### Training
After you have given the required information to the manager and prepared your trainingdata,
 you can start the training. This is done by typing the following command
 
    manager.startTraining(trainingData);
    
##### Testing
When the training is done, you can try giving the AI some input, and see what it outputs.
This is done by the following command
    
    manager.testNeuralNetwork(trainingData);


## Example
This is an example that collects all the images in *'dataA'*, *'dataB'* and *'test'*.
The images are then given to the AI, which will be trained for 1000 iterations. Then the AI
is tested with the last *'test'* folder. The AI will make a prediction, and the prediction is printed.
```java
import com.zostio.myai.training.*;
import java.util.ArrayList;

public class MyApplication {

    private NeuralNetworkManager manager;
    private ArrayList<TrainingData> trainingData;
    private ArrayList<TrainingData> testData;

    private int possibleOutcomes = 2;
    
    public static void main(String[] args) {
        /*
        The number 16 tells the AI how many neurons there should be in every layer.
        With the following configuration, the AI will create 2 hidden layers,
        with 16 neurons each. The training will run for 1000 iterations.
        */
        manager = new NeuralNetworkManager(new int[]{16,16}, possibleOutcomes, 1000);
        prepareTrainingData();
        trainAI();
        testAI();
    }

    public void prepareTrainingData() {
        trainingData = new ArrayList<>();
        testData = new ArrayList<>();

        //Here you can prepare your training data
        TrainingDataEncoder encoder = new TrainingDataEncoder();
        encoder.setImageResize(100);

        trainingData = encoder.getTrainingDataset("C:/myuser/documents/ai/dataA", 
                new double[]{0,1});
        trainingData.addAll(encoder.getTrainingDataset("C:/myuser/documents/ai/dataB", 
                new double[]{1,0}));
        testData = encoder.getTrainingDataSet("C:/myuser/documents/ai/test", 
                new double[]{0,1});
    }

    public void trainAI() {
        manager.startTraining(trainingData);
    }
    
    public void testAI() {
        //this will try to send the numbers 0 and 1 through the AI, and see what it predicts.
        //usually you would send some data that it can make predictions to based on the training.
        for (int i = 0; i < testData.size();i++) {
            double[] AIPred = manager.testNeuralNetwork(testData.get(i).getData());
            System.out.println("The AI predicted: " + AIPred[0]);
        }

    }   

}
```

# Markdown
You can emphasize text with **bold**, *italic* or ***both***

##Subheading

#### Subsubheading

Use a [reference link][1]

[1]: src/com/zostio/myai/networkcomponents/

```java
class Hello {
    public static void main(String[] args) {
        System.out.println("");
    }
}
```

    injection