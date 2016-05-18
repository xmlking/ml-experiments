import mnist from 'mnist';
import { Layer, Network, Trainer } from 'synaptic';
import Perceptron from './utils/perceptron';

const set = mnist.set(700, 20);
const trainingSet = set.training;
const testSet = set.test;

let myPerceptron = new Perceptron(784,100,10);
myPerceptron.connectLayers();

const trainer = new Trainer(myPerceptron);
trainer.train(trainingSet, {
    rate: .2,
    iterations: 20,
    error: .1,
    shuffle: true,
    log: 1,
    cost: Trainer.cost.CROSS_ENTROPY
});

console.log(myPerceptron.activate(testSet[0].input));
console.log(testSet[0].output);
console.log("done")
