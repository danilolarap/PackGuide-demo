import catalogData from './catalog.json' with {type:'json'};
export type BagType = 'MANO'|'CABINA'|'BODEGA';
export const bagTypes: BagType[] = ['MANO','CABINA','BODEGA'];
export const zones = ['BASE','CENTRO','ACCESO RÁPIDO'] as const;
export type Zone = typeof zones[number];
export type CatalogItem = {id:string;name:string;category:string;dimensions:number[];unitVolumeL:number;icon:string;quantity:number};
export type Item = CatalogItem & {packedQuantity:number;packedUnits:boolean[];zone:Zone};
export type Bag = {type:BagType;dimensions:number[];confirmed:boolean;items:Item[]};
export type Packing = {bags:Bag[];active:BagType};
export const catalog:CatalogItem[] = catalogData;
export const presets:Record<BagType,number[]> = {MANO:[45,35,20],CABINA:[55,35,25],BODEGA:[75,50,30]};
export function makeBag(type:BagType,initial=false):Bag {return {type,dimensions:[...presets[type]],confirmed:false,items:catalog.map(i=>({...i,quantity:initial?i.quantity:0,packedQuantity:0,packedUnits:Array(initial?i.quantity:0).fill(false),zone:'BASE'}))};}
export function initialPacking():Packing {return {bags:[makeBag('CABINA',true)],active:'CABINA'};}
// Integer milliliters preserve the reference's decimal arithmetic and exact capacity comparisons.
export function metrics(bag:Bag) {
  const capacityMl = bag.dimensions.reduce((a,b)=>a*b,1);
  const usedMl = bag.items.reduce((n,i)=>n+i.quantity*Math.round(i.unitVolumeL*1000),0);
  const selected = bag.items.reduce((n,i)=>n+i.quantity,0), packed=bag.items.reduce((n,i)=>n+i.packedQuantity,0);
  return {capacity:capacityMl/1000,used:usedMl/1000,free:Math.max(0,capacityMl-usedMl)/1000,excess:Math.max(0,usedMl-capacityMl)/1000,over:usedMl>capacityMl,occupancy:usedMl/capacityMl,percent:Math.trunc(usedMl*100/capacityMl),segments:Math.min(10,Math.trunc(usedMl*10/capacityMl)),selected,packed,progress:selected?Math.trunc(packed*100/selected):0};
}
export function liters(n:number) {return n>0&&n<0.05?'<0.1':(Math.round((n+Number.EPSILON)*10)/10).toFixed(1);}
export function changeQuantity(bag:Bag,id:string,delta:number):Bag {return {...bag,items:bag.items.map(i=>{if(i.id!==id)return i;const q=Math.max(0,i.quantity+delta);const packedUnits=i.packedUnits.slice(0,q);while(packedUnits.length<q)packedUnits.push(false);return {...i,quantity:q,packedUnits,packedQuantity:packedUnits.filter(Boolean).length};})};}
export function setPackedUnit(bag:Bag,id:string,index:number,checked:boolean):Bag {return {...bag,items:bag.items.map(i=>{if(i.id!==id||index<0||index>=i.quantity)return i;const packedUnits=[...i.packedUnits];packedUnits[index]=checked;return {...i,packedUnits,packedQuantity:packedUnits.filter(Boolean).length};})};}
export function markPacked(bag:Bag,id:string):Bag {return {...bag,items:bag.items.map(i=>{if(i.id!==id)return i;const packedUnits=i.packedQuantity===i.quantity?i.packedUnits.map(()=>false):i.packedUnits.map((v,index)=>index===i.packedUnits.findIndex(p=>!p)?true:v);return {...i,packedUnits,packedQuantity:packedUnits.filter(Boolean).length};})};}
export function cycleZone(bag:Bag,id:string):Bag {return {...bag,items:bag.items.map(i=>i.id===id?{...i,zone:zones[(zones.indexOf(i.zone)+1)%3]}:i)};}
export function planKey(bag:Bag) {return JSON.stringify([bag.dimensions,bag.items.filter(i=>i.quantity>0).map(i=>[i.id,i.quantity,i.zone])]);}
