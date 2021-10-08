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
                        <th colspan="3" rowspan="2">Трудоемкость ООП (учебная нагрузка)</th>

                        <th colspan="12">Распределение учебного времени по видам учебных занятий</th>
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
                        <th rowspan="3" part="rotated-head">лекции</th>
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
                        <th rowspan="2" part="rotated-head">Весовой коэффициент</th>
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
                    <template is="dom-repeat" items="{{blocks}}" as="block">
                        <tr>
                            <th colspan="58">{{block.head}}</th>
                        </tr>
                        <template is="dom-repeat" items="{{block.articles}}" as="article">
                            <tr>
                                <th colspan="58">{{article.head}}</th>
                            </tr>
                            <template is="dom-repeat" items="{{article.rows}}" as="item">
                                <tr id="{{item.id}}">
                                    <td><a href="tutor/syllabus/edit/{{item.id}}">{{item.index}}</a></td>
                                    <td>{{item.discipline}}</td>
                                    <td>{{item.bFertileUnits}}</td>
                                    <td>{{item.vFertileUnits}}</td>
                                    <td>{{item.totalHours}}</td>
                                    <td>{{item.hoursWithTeacher}}</td>
                                    <td>{{item.fertileUnits}}</td>
                                    <td>{{item.totalHours}}</td>
                                    <td>{{item.undefiningParameter}}</td>
                                    <td>{{item.lectures}}</td>
                                    <td>{{item.seminars}}</td>
                                    <td>{{item.groupExercises}}</td>
                                    <td>{{item.groupLessons}}</td>
                                    <td>{{item.laboratoryWorks}}</td>
                                    <td>{{item.practicalLessons}}</td>
                                    <td>{{item.specialLessons}}</td>
                                    <td>{{item.courseWorks}}</td>
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


                                    <th>{{item.examControl}}</th>
                                    <th>{{item.gradedCreditControl}}</th>
                                    <th>{{item.passWithoutAssessmentControl}}</th>
                                    <th>{{item.courseWorkControl}}</th>
                                </tr>
                            </template>
                            <tr>
                                <th colspan="2">{{article.total.discipline}}</th>
                                <td>{{article.total.bFertileUnits}}</td>
                                <td>{{article.total.vFertileUnits}}</td>
                                <td>{{article.total.totalHours}}</td>
                                <td>{{article.total.hoursWithTeacher}}</td>
                                <td>{{article.total.fertileUnits}}</td>
                                <td>{{article.total.totalHours}}</td>

                                <td>{{article.total.undefiningParameter}}</td>
                                <td>{{article.total.lectures}}</td>
                                <td>{{article.total.seminars}}</td>
                                <td>{{article.total.groupExercises}}</td>
                                <td>{{article.total.groupLessons}}</td>
                                <td>{{article.total.laboratoryWorks}}</td>
                                <td>{{article.total.practicalLessons}}</td>
                                <td>{{article.total.specialLessons}}</td>
                                <td>{{article.total.courseWorks}}</td>
                                <td>{{article.total.conferences}}</td>
                                <td>{{article.total.practices}}</td>
                                <td>{{article.total.tests}}</td>
                                <td>{{article.total.credit}}</td>
                                <td>{{article.total.selfTraningHours}}</td>
                                <td>{{article.total.examHours}}</td>


                                <td>{{article.total.y1s1i}}</td>
                                <td>{{article.total.y1s1t}}</td>
                                <td>{{article.total.y1s1s}}</td>
                                <td>{{article.total.y1s2i}}</td>
                                <td>{{article.total.y1s2t}}</td>
                                <td>{{article.total.y1s2s}}</td>

                                <td>{{article.total.y2s1i}}</td>
                                <td>{{article.total.y2s1t}}</td>
                                <td>{{article.total.y2s1s}}</td>
                                <td>{{article.total.y2s2i}}</td>
                                <td>{{article.total.y2s2t}}</td>
                                <td>{{article.total.y2s2s}}</td>

                                <td>{{article.total.y3s1i}}</td>
                                <td>{{article.total.y3s1t}}</td>
                                <td>{{article.total.y3s1s}}</td>
                                <td>{{article.total.y3s2i}}</td>
                                <td>{{article.total.y3s2t}}</td>
                                <td>{{article.total.y3s2s}}</td>

                                <td>{{article.total.y4s1i}}</td>
                                <td>{{article.total.y4s1t}}</td>
                                <td>{{article.total.y4s1s}}</td>
                                <td>{{article.total.y4s2i}}</td>
                                <td>{{article.total.y4s2t}}</td>
                                <td>{{article.total.y4s2s}}</td>

                                <td>{{article.total.y5s1i}}</td>
                                <td>{{article.total.y5s1t}}</td>
                                <td>{{article.total.y5s1s}}</td>
                                <td>{{article.total.y5s2i}}</td>
                                <td>{{article.total.y5s2t}}</td>
                                <td>{{article.total.y5s2s}}</td>


                                <th>{{article.total.examControl}}</th>
                                <th>{{article.total.gradedCreditControl}}</th>
                                <th>{{article.total.passWithoutAssessmentControl}}</th>
                                <th>{{article.total.courseWorkControl}}</th>
                            </tr>
                        </template>
                        <tr>
                            <th colspan="2">Итого за цикл</th>
                            <th>{{block.total.bFertileUnits}}</th>
                            <th>{{block.total.vFertileUnits}}</th>
                            <th>{{block.total.totalHours}}</th>
                            <th>{{block.total.hoursWithTeacher}}</th>
                            <th>{{block.total.fertileUnits}}</th>
                            <th>{{block.total.totalHours}}</th>
                            <th>{{block.total.undefiningParameter}}</th>
                            <th>{{block.total.lectures}}</th>
                            <th>{{block.total.seminars}}</th>
                            <th>{{block.total.groupExercises}}</th>
                            <th>{{block.total.groupLessons}}</th>
                            <th>{{block.total.laboratoryWorks}}</th>
                            <th>{{block.total.practicalLessons}}</th>
                            <th>{{block.total.specialLessons}}</th>
                            <th>{{block.total.courseWorks}}</th>
                            <th>{{block.total.conferences}}</th>
                            <th>{{block.total.practices}}</th>
                            <th>{{block.total.tests}}</th>
                            <th>{{block.total.credit}}</th>
                            <th>{{block.total.selfTraningHours}}</th>
                            <th>{{block.total.examHours}}</th>


                            <th>{{block.total.y1s1i}}</th>
                            <th>{{block.total.y1s1t}}</th>
                            <th>{{block.total.y1s1s}}</th>
                            <th>{{block.total.y1s2i}}</th>
                            <th>{{block.total.y1s2t}}</th>
                            <th>{{block.total.y1s2s}}</th>

                            <th>{{block.total.y2s1i}}</th>
                            <th>{{block.total.y2s1t}}</th>
                            <th>{{block.total.y2s1s}}</th>
                            <th>{{block.total.y2s2i}}</th>
                            <th>{{block.total.y2s2t}}</th>
                            <th>{{block.total.y2s2s}}</th>

                            <th>{{block.total.y3s1i}}</th>
                            <th>{{block.total.y3s1t}}</th>
                            <th>{{block.total.y3s1s}}</th>
                            <th>{{block.total.y3s2i}}</th>
                            <th>{{block.total.y3s2t}}</th>
                            <th>{{block.total.y3s2s}}</th>

                            <th>{{block.total.y4s1i}}</th>
                            <th>{{block.total.y4s1t}}</th>
                            <th>{{block.total.y4s1s}}</th>
                            <th>{{block.total.y4s2i}}</th>
                            <th>{{block.total.y4s2t}}</th>
                            <th>{{block.total.y4s2s}}</th>

                            <th>{{block.total.y5s1i}}</th>
                            <th>{{block.total.y5s1t}}</th>
                            <th>{{block.total.y5s1s}}</th>
                            <th>{{block.total.y5s2i}}</th>
                            <th>{{block.total.y5s2t}}</th>
                            <th>{{block.total.y5s2s}}</th>


                            <th>{{block.total.examControl}}</th>
                            <th>{{block.total.gradedCreditControl}}</th>
                            <th>{{block.total.passWithoutAssessmentControl}}</th>
                            <th>{{block.total.courseWorkControl}}</th>
                        </tr>
                    </template>
                    <tr>
                        <th colspan="2">Итого за специальность</th>
                        <th>{{total.bFertileUnits}}</th>
                        <th>{{total.vFertileUnits}}</th>
                        <th>{{total.totalHours}}</th>
                        <th>{{total.hoursWithTeacher}}</th>
                        <th>{{total.fertileUnits}}</th>
                        <th>{{total.totalHours}}</th>
                        <th>{{total.undefiningParameter}}</th>
                        <th>{{total.lectures}}</th>
                        <th>{{total.seminars}}</th>
                        <th>{{total.groupExercises}}</th>
                        <th>{{total.groupLessons}}</th>
                        <th>{{total.laboratoryWorks}}</th>
                        <th>{{total.practicalLessons}}</th>
                        <th>{{total.specialLessons}}</th>
                        <th>{{total.courseWorks}}</th>
                        <th>{{total.conferences}}</th>
                        <th>{{total.practices}}</th>
                        <th>{{total.tests}}</th>
                        <th>{{total.credit}}</th>
                        <th>{{total.selfTraningHours}}</th>
                        <th>{{total.examHours}}</th>


                        <th>{{total.y1s1i}}</th>
                        <th>{{total.y1s1t}}</th>
                        <th>{{total.y1s1s}}</th>
                        <th>{{total.y1s2i}}</th>
                        <th>{{total.y1s2t}}</th>
                        <th>{{total.y1s2s}}</th>

                        <th>{{total.y2s1i}}</th>
                        <th>{{total.y2s1t}}</th>
                        <th>{{total.y2s1s}}</th>
                        <th>{{total.y2s2i}}</th>
                        <th>{{total.y2s2t}}</th>
                        <th>{{total.y2s2s}}</th>

                        <th>{{total.y3s1i}}</th>
                        <th>{{total.y3s1t}}</th>
                        <th>{{total.y3s1s}}</th>
                        <th>{{total.y3s2i}}</th>
                        <th>{{total.y3s2t}}</th>
                        <th>{{total.y3s2s}}</th>

                        <th>{{total.y4s1i}}</th>
                        <th>{{total.y4s1t}}</th>
                        <th>{{total.y4s1s}}</th>
                        <th>{{total.y4s2i}}</th>
                        <th>{{total.y4s2t}}</th>
                        <th>{{total.y4s2s}}</th>

                        <th>{{total.y5s1i}}</th>
                        <th>{{total.y5s1t}}</th>
                        <th>{{total.y5s1s}}</th>
                        <th>{{total.y5s2i}}</th>
                        <th>{{total.y5s2t}}</th>
                        <th>{{total.y5s2s}}</th>


                        <th>{{total.examControl}}</th>
                        <th>{{total.gradedCreditControl}}</th>
                        <th>{{total.passWithoutAssessmentControl}}</th>
                        <th>{{total.courseWorkControl}}</th>
                    </tr>
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
