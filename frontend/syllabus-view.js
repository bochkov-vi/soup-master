import {html, PolymerElement} from '@polymer/polymer/polymer-element.js';

class SyllabusView extends PolymerElement {

    static get template() {
        return html`
            <style include="shared-styles">
                :host {
                    display: block;
                    height: 100%;
                    padding: 0;
                    margin: 0;
                }

                [part="table"], [part="table"] th, [part="table"] td {
                    border: 1px solid black;
                    border-collapse: collapse;
                    padding: 3px;
                }

                [part="rotated-head"] {
                    -webkit-transform: rotate(-180deg);
                    -moz-transform: rotate(-180deg);
                    -ms-transform: rotate(-180deg);
                    -o-transform: rotate(-180deg);
                    transform: rotate(-180deg);
                    writing-mode: vertical-lr;
                }

            </style>
            <div style="width: 100%;overflow-x:auto">
                <table part="table" style="overflow-y: auto;width: 100% ">
                    <thead>
                    <tr>
                        <th rowspan="4">Индекс</th>
                        <th rowspan="4">Наименование учебных циклов, дисциплин (модулей), разделов</th>
                        <th rowspan="2" colspan="2">Трудоемкость (в зачетных единицах)</th>
                        <th rowspan="4">
                            <div part="rotated-head">Всего, в часах</div>
                        </th>
                        <th rowspan="4">
                            <div part="rotated-head">Учебные занаятия с преподавателем, в часах</div>
                        </th>
                        <th colspan="5" rowspan="2">Трудоемкость ООП (учебная нагрузка)</th>

                        <th colspan="11">Распределение учебного времени по видам учебных занятий</th>
                        <th rowspan="4">
                            <div part="rotated-head">Время, отводимое на самостоятельную работу</div>
                        </th>
                        <th rowspan="4">
                            <div part="rotated-head">Время, отводимое на экзамены и зачеты (выносимые на сессию)
                            </div>
                        </th>
                        <th colspan="30">Распределение учебного времени по курсам и семестрам</th>
                        <th colspan="4">Формы промежуточного и итогового контроля</th>
                    </tr>
                    <tr>
                        <th rowspan="3" part="rotated-head">семинары</th>
                        <th rowspan="3" part="rotated-head">групповые упражнения</th>
                        <th rowspan="3" part="rotated-head">групповые занятия</th>
                        <th rowspan="3" part="rotated-head">лабораторные работы</th>
                        <th rowspan="3" part="rotated-head">практические занятия</th>
                        <th rowspan="3" part="rotated-head">тактические (тактико-специальные) занятия и учения</th>
                        <th rowspan="3" part="rotated-head">курсовые рабoты(проекты, задачи)</th>
                        <th rowspan="3" part="rotated-head">научно-практическая конференция</th>
                        <th rowspan="3" part="rotated-head">практика</th>
                        <th rowspan="3" part="rotated-head">контрольные работы(занятия)</th>
                        <th rowspan="3" part="rotated-head">зачеты</th>
                        <th colspan="6">1 курс</th>
                        <th colspan="6">2 курс</th>
                        <th colspan="6">3 курс</th>
                        <th colspan="6">4 курс</th>
                        <th colspan="6">5 курс</th>
                        <th rowspan="3" part="rotated-head">экзамены</th>
                        <th colspan="3">зачеты</th>

                    </tr>
                    <tr>
                        <th rowspan="2">
                            <div part="rotated-head">Базовая часть</div>
                        </th>
                        <th rowspan="2">
                            <div part="rotated-head">Вариативная часть</div>
                        </th>
                        <th rowspan="2" part="rotated-head">зачетные единицы</th>
                        <th rowspan="2" part="rotated-head">часы</th>
                        <th rowspan="2" part="rotated-head">-</th>
                        <th rowspan="2" part="rotated-head">-</th>
                        <th rowspan="2" part="rotated-head">-</th>
                        <th colspan="3">1 семестр</th>
                        <th colspan="3">2 семестр</th>
                        <th colspan="3">3 семестр</th>
                        <th colspan="3">4 семестр</th>
                        <th colspan="3">5 семестр</th>
                        <th colspan="3">6 семестр</th>
                        <th colspan="3">7 семестр</th>
                        <th colspan="3">8 семестр</th>
                        <th colspan="3">9 семестр</th>
                        <th colspan="3">10 семестр</th>
                        <th rowspan="2" part="rotated-head">с оценкой</th>
                        <th rowspan="2" part="rotated-head">без оценки</th>
                        <th rowspan="2" part="rotated-head">по курсовым работам (проектам и задачам)</th>

                    </tr>
                    <tr>
                        <th part="rotated-head">зачетные единицы</th>
                        <th part="rotated-head">часы учебных занятий</th>
                        <th part="rotated-head">часы на СР</th>
                        <th part="rotated-head">зачетные единицы</th>
                        <th part="rotated-head">часы учебных занятий</th>
                        <th part="rotated-head">часы на СР</th>
                        <th part="rotated-head">зачетные единицы</th>
                        <th part="rotated-head">часы учебных занятий</th>
                        <th part="rotated-head">часы на СР</th>
                        <th part="rotated-head">зачетные единицы</th>
                        <th part="rotated-head">часы учебных занятий</th>
                        <th part="rotated-head">часы на СР</th>
                        <th part="rotated-head">зачетные единицы</th>
                        <th part="rotated-head">часы учебных занятий</th>
                        <th part="rotated-head">часы на СР</th>
                        <th part="rotated-head">зачетные единицы</th>
                        <th part="rotated-head">часы учебных занятий</th>
                        <th part="rotated-head">часы на СР</th>
                        <th part="rotated-head">зачетные единицы</th>
                        <th part="rotated-head">часы учебных занятий</th>
                        <th part="rotated-head">часы на СР</th>
                        <th part="rotated-head">зачетные единицы</th>
                        <th part="rotated-head">часы учебных занятий</th>
                        <th part="rotated-head">часы на СР</th>
                        <th part="rotated-head">зачетные единицы</th>
                        <th part="rotated-head">часы учебных занятий</th>
                        <th part="rotated-head">часы на СР</th>
                        <th part="rotated-head">зачетные единицы</th>
                        <th part="rotated-head">часы учебных занятий</th>
                        <th part="rotated-head">часы на СР</th>
                    </tr>
                    </thead>
                    <tbody>
                    <template is="dom-repeat" items="[[syllabuses]]">
                        <tr>
                            <td>{{item.index}}</td>
                            <td>{{item.discipline}}</td>
                            <td>{{item.bFertileUnits}}</td>
                            <td>{{item.vFertileUnits}}</td>
                            <td>{{item.totalHours}}</td>
                            <td>{{item.hoursWithTeacher}}</td>
                            <td>{{item.fertileUnits}}</td>
                            <td>{{item.totalHours}}</td>


                            <td></td>
                            <td></td>
                            <td></td>

                            <td>{{item.lectures}}</td>
                            <td>{{item.seminars}}</td>
                            <td>{{item.groupExercises}}</td>
                            <td>{{item.groupLessons}}</td>
                            <td>{{item.laboratoryWorks}}</td>
                            <td>{{item.practicalLessons}}</td>
                            <td>{{item.specialLessons}}</td>
                            <td>{{item.conferences}}</td>
                            <td>{{item.practices}}</td>
                            <td>{{item.tests}}</td>
                            <td>{{item.credit}}</td>
                            <td>{{item.selfTraningHours}}</td>
                            <td>{{item.examHours}}</td>


                            <td>{{item.y1s1i}}</td>
                            <td>{{item.y1s1t}}</td>
                            <td>{{item.y1s1s}}</td>
                            <td>{{item.y1s2i}}</td>
                            <td>{{item.y1s2t}}</td>
                            <td>{{item.y1s2s}}</td>

                            <td>{{item.y2s1i}}</td>
                            <td>{{item.y2s1t}}</td>
                            <td>{{item.y2s1s}}</td>
                            <td>{{item.y2s2i}}</td>
                            <td>{{item.y2s2t}}</td>
                            <td>{{item.y2s2s}}</td>

                            <td>{{item.y3s1i}}</td>
                            <td>{{item.y3s1t}}</td>
                            <td>{{item.y3s1s}}</td>
                            <td>{{item.y3s2i}}</td>
                            <td>{{item.y3s2t}}</td>
                            <td>{{item.y3s2s}}</td>

                            <td>{{item.y4s1i}}</td>
                            <td>{{item.y4s1t}}</td>
                            <td>{{item.y4s1s}}</td>
                            <td>{{item.y4s2i}}</td>
                            <td>{{item.y4s2t}}</td>
                            <td>{{item.y4s2s}}</td>

                            <td>{{item.y5s1i}}</td>
                            <td>{{item.y5s1t}}</td>
                            <td>{{item.y5s1s}}</td>
                            <td>{{item.y5s2i}}</td>
                            <td>{{item.y5s2t}}</td>
                            <td>{{item.y5s2s}}</td>
                        </tr>
                    </template>
                    </tbody>
                </table>
            </div>
        `;
    }

    static get is() {
        return 'syllabus-view';
    }

    static get properties() {
        return {
            baseFertileUnits(item) {
                return item.base ? item.fertileUnits : ''
            },
            variativeFertileUnits(item) {
                return !item.base ? item.fertileUnits : ''
            },
        };
    }
}

customElements.define(SyllabusView.is, SyllabusView);
