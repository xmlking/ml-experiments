import { Layer, Network, Trainer } from 'synaptic';

export default class Perceptron extends Network
{
    constructor(input, hidden, output) {
        super();
        this.inputLayer = new Layer(input);
        this.hiddenLayer = new Layer(hidden);
        this.outputLayer = new Layer(output);
    }
    connectLayers() {
        this.inputLayer.project(this.hiddenLayer);
        this.hiddenLayer.project(this.outputLayer);

        this.set({
            input: this.inputLayer,
            hidden: [this.hiddenLayer],
            output: this.outputLayer
        });
    }
}
