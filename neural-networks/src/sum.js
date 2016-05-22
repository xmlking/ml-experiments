import { Trainer } from 'synaptic';
import Perceptron from './utils/perceptron';
import assert from 'assert';
import {parse, fromNumber } from  './utils/binary';
import trainingSet from './trainingSet';


let myPerceptron = new Perceptron(8, 64, 8);
myPerceptron.connectLayers();

const myTrainer = new Trainer(myPerceptron);
myTrainer.train(trainingSet, {
    rate: .1,
    iterations: 100000,
    shuffle: true,
    cost: Trainer.cost.CROSS_ENTROPY,
    log: 100
});

assert.equal(parse(myPerceptron.activate(fromNumber(7, 4).concat(fromNumber(12, 4)))), 19);
assert.equal(parse(myPerceptron.activate(fromNumber(9, 4).concat(fromNumber(4, 4)))), 13);
assert.equal(parse(myPerceptron.activate(fromNumber(1, 4).concat(fromNumber(15, 4)))), 16);
assert.equal(parse(myPerceptron.activate(fromNumber(2, 4).concat(fromNumber(0, 4)))), 2);
assert.equal(parse(myPerceptron.activate(fromNumber(15, 4).concat(fromNumber(15, 4)))), 30);
