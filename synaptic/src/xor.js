import Perceptron from './perceptron';
import { Trainer } from 'synaptic';

let myPerceptron = new Perceptron(2,20,1);
myPerceptron.connectLayers();
const myTrainer = new Trainer(myPerceptron);

console.log(myTrainer.XOR());

console.log('--');
console.log('0 ,0: ' + myPerceptron.activate([0,0]));
console.log('1 ,0: ' + myPerceptron.activate([1,0]));
console.log('0 ,1: ' + myPerceptron.activate([0,1]));
console.log('1 ,1: ' + myPerceptron.activate([1,1]));
console.log('--');
