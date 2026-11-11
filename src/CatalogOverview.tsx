import {Badge,Button} from './components';
import {useCopy} from './i18n';
import {metrics} from './model';
import type {Bag,BagType} from './model';

export function CatalogOverview({bags,active,onGuide}:{bags:Bag[];active:BagType;onGuide:()=>void}){
 const {t,choose,volume,balance}=useCopy();
 return <section className="capacity-tracker" aria-label={choose('Capacidad de las maletas','Bag capacities')}><div className="capacity-list">{bags.map(b=>{const m=metrics(b);return <div className={`capacity-bag ${b.type===active?'active':''}`} key={b.type}><div className="row gap8"><strong className="label-medium grow">{t(b.type)}{b.type===active?choose(' · ACTIVA',' · ACTIVE'):''}</strong><span className="label-small">{m.percent}%</span></div><div className="segments" aria-label={`${m.percent}%`}>{Array.from({length:10},(_,i)=><i key={i} className={i<m.segments?'filled':''}/>)}</div><div className="row gap8"><span className="body-small grow">{volume(b)}</span><Badge over={m.over}>{balance(b)}</Badge></div></div>;})}</div><Button icon="arrow_forward" onClick={onGuide}>{t('CREAR PLAN DE EMPACADO')}</Button></section>;
}
