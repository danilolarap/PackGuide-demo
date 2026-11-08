import {createContext,useContext} from 'react';
import translations from './translations.json';
import common from './common-terms.json';
import {liters,metrics} from './model';
import type {Bag} from './model';
export type Language = 'es'|'en';
export const LanguageContext=createContext<Language>('es');
const dictionary:Record<string,string>=translations;
export function translate(text:string,language:Language):string {
 if(language!=='en')return text;
 if(dictionary[text]!==undefined)return dictionary[text];
 return common.reduce((result,[word,value])=>result.replace(new RegExp(word,'gi'),value),text);
}
export function useCopy(){
 const lang=useContext(LanguageContext);const t=(s:string)=>translate(s,lang);const choose=(es:string,en:string)=>lang==='en'?en:es;
 return {lang,t,choose,volume:(bag:Bag)=>{const m=metrics(bag);return `${liters(m.used)} L / ${liters(m.capacity)} L`;},balance:(bag:Bag)=>{const m=metrics(bag);return (m.over?choose('EXCESO EST.: ','EST. EXCESS: '):choose('ESPACIO EST.: ','EST. FREE SPACE: '))+liters(m.over?m.excess:m.free)+' L';},counts:(bag:Bag)=>{const m=metrics(bag);return choose(`${m.selected} unidades seleccionadas · ${m.packed} empacadas`,`${m.selected} units selected · ${m.packed} packed`);}};
}
