import test from 'node:test';
import assert from 'node:assert/strict';
import {catalog,changeQuantity,cycleZone,initialPacking,liters,makeBag,markPacked,metrics,planKey} from '../src/model.ts';
import {validation} from '../src/auth.ts';
test('reference inventory and initial estimates remain exact',()=>{
 const p=initialPacking(),b=p.bags[0],m=metrics(b);
 assert.equal(catalog.length,36);assert.equal(p.active,'CABINA');assert.deepEqual(b.dimensions,[55,35,25]);assert.equal(b.confirmed,false);
 assert.deepEqual(b.items.slice(0,6).map(i=>i.quantity),[3,2,1,1,1,1]);assert.ok(b.items.slice(6).every(i=>i.quantity===0));
 assert.equal(m.selected,9);assert.equal(m.packed,0);assert.equal(m.used,19.2);assert.equal(m.capacity,48.125);assert.equal(liters(m.capacity),'48.1');assert.equal(liters(m.free),'28.9');assert.equal(m.percent,39);assert.equal(m.segments,3);
});
test('packing advances by units, resets after completion and clamps on decrement',()=>{
 let b=initialPacking().bags[0];b=markPacked(b,'ITEM-01');assert.equal(b.items[0].packedQuantity,1);assert.equal(metrics(b).progress,11);
 b=markPacked(markPacked(b,'ITEM-01'),'ITEM-01');assert.equal(b.items[0].packedQuantity,3);
 b=changeQuantity(b,'ITEM-01',-1);assert.equal(b.items[0].packedQuantity,2);assert.equal(b.items[0].quantity,2);
 b=markPacked(b,'ITEM-01');assert.equal(b.items[0].packedQuantity,0);
 for(let i=0;i<5;i++)b=changeQuantity(b,'ITEM-01',-1);assert.equal(b.items[0].quantity,0);
});
test('bags are independent and plan changes exclude packing checkmarks',()=>{
 let a=makeBag('MANO'),b=makeBag('BODEGA');a=changeQuantity(a,'ITEM-01',3);assert.equal(b.items[0].quantity,0);
 const key=planKey(a);assert.equal(planKey(markPacked(a,'ITEM-01')),key);
 a=cycleZone(a,'ITEM-01');assert.equal(a.items[0].zone,'CENTRO');assert.notEqual(planKey(a),key);
 a=cycleZone(cycleZone(a,'ITEM-01'),'ITEM-01');assert.equal(a.items[0].zone,'BASE');
});
test('capacity equality uses decimal-safe milliliters and excess is strictly greater',()=>{
 let b=makeBag('CABINA');b.dimensions=[10,10,3];b=changeQuantity(b,'ITEM-07',1);
 assert.equal(metrics(b).used,.3);assert.equal(metrics(b).capacity,.3);assert.equal(metrics(b).over,false);assert.equal(metrics(b).percent,100);
 b=changeQuantity(b,'ITEM-07',1);assert.equal(metrics(b).over,true);assert.equal(metrics(b).percent,200);assert.equal(metrics(b).segments,10);
 assert.equal(metrics(makeBag('MANO')).progress,0);assert.equal(liters(.024),'<0.1');assert.equal(liters(0),'0.0');
});
test('registration preserves the reference validation, including one-character Unicode passwords',()=>{
 assert.equal(validation('dan','ñ',true,'ñ'),null);assert.equal(validation('  dan  ','1',true,'1'),null);
 assert.equal(validation('a','x',true,'x'),'El usuario debe tener al menos 3 caracteres.');
 assert.equal(validation('dan','a!',true,'a!'),'Usa solo letras y números en la contraseña.');
 assert.equal(validation('dan','1',true,'2'),'Las contraseñas no coinciden.');
 assert.equal(validation('dan','',false),'Ingresa tu contraseña.');
});
