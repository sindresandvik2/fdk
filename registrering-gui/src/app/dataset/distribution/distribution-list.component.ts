import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';

import { Distribution } from './distribution';
import { Dataset } from '../dataset';
import {DistributionFormComponent} from './distribution.component';

@Component({
    selector: 'distributions',
    templateUrl: './distribution-list.component.html'
})
export class DistributionListComponent implements OnInit {
    @Input('datasetForm')
    public datasetForm: FormGroup;

    @Input('distributions')
    public distributions: Distribution[];

    distribution: Distribution;

    constructor(private cd: ChangeDetectorRef) { }

    ngOnInit() {
      console.log('this.distributions', this.distributions);
    }

    addDistribution() {
        const distribution: Distribution = {
            id: Math.floor(Math.random() * 1000000).toString(),
            uri: '',
            title: {nb:''},
            description: {nb:''},
            accessUrl: [],
            license: '',
            format: [],
            downloadUrl: []
        };
        //this.distributions = this.distributions || [];
        this.distributions.push(distribution);
        //this.cd.detectChanges();
        return false;
    }

    removeDistribution(idx: number) {
      console.log('removeDistribution');
        if (this.distributions.length > 0) {
            this.distributions.splice(idx, 1);
            (<FormArray>this.datasetForm.get('distributions')).removeAt(idx);
        }
        return false;
    }
}
