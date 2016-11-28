/***************************************************************************

Copyright (c) 2016, EPAM SYSTEMS INC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

****************************************************************************/

import { Component, Input, Output, ViewChild, OnInit } from "@angular/core";
import { UserResourceService } from "./../../services/userResource.service";
import { ResourcesGridRowModel } from './resources-grid.model';
import { FilterConfigurationModel } from './filterConfiguration.model';
import { CreateEmrModel } from "./createEmrModel";
import { ConfirmationDialogType } from "../confirmation-dialog/confirmation-dialog-type.enum";

@Component({
  moduleId: module.id,
  selector: 'resources-grid',
  templateUrl: 'resources-grid.component.html',
  styleUrls: ['./resources-grid.component.css']
})

export class ResourcesGrid implements OnInit {

  environments: Array<ResourcesGridRowModel>;

  filterConfiguration: FilterConfigurationModel;
  filterForm: FilterConfigurationModel = new FilterConfigurationModel('', [], [], '');

  filteredEnvironments: Array<ResourcesGridRowModel>;
  model = new CreateEmrModel('', '');
  notebookName: string;
  isOutscreenDropdown: boolean;

  collapseFilterRow: boolean = false;
  filtering: boolean = false;

  @ViewChild('computationalResourceModal') computationalResourceModal;
  @ViewChild('confirmationDialog') confirmationDialog;
  @ViewChild('detailDialog') detailDialog;

  constructor(
    private userResourceService: UserResourceService
  ) { }

  public filteringColumns:Array<any> = [
    {title: 'Environment name', name: 'name', className: 'th_name', filtering: {}},
    {title: 'Status', name: 'status', className: 'th_status', filtering: {}},
    {title: 'Shape', name: 'shape', className: 'th_shape', filtering: {}},
    {title: 'Computational resources', name: 'resources', className: 'th_resources', filtering: {}},
    {title: 'Actions', className: 'th_actions'}
  ];

  ngOnInit() : void {
    this.buildGrid();
  }

  toggleFilterRow() : void {
    this.collapseFilterRow = !this.collapseFilterRow;
  }
  getDefaultFilterConfiguration() : void {
    let data:Array<ResourcesGridRowModel> = this.environments;
    let shapes = [], statuses = [];

    data.forEach((item:any) => {
      if(shapes.indexOf(item.shape) == -1)
      shapes.push(item.shape);
      if(statuses.indexOf(item.status) == -1)
      statuses.push(item.status)
    });

    this.filterConfiguration = new FilterConfigurationModel('', statuses, shapes, '');
  }
  applyFilter_btnClick() {
    console.log(this.filterForm);

    let filteredData:Array<any> = this.environments;
    this.filtering = true;

    filteredData = filteredData.filter((item:any) => {
       console.log(this.filterForm);
       return item.name.toLowerCase().match(this.filterForm.name.toLowerCase())
              && (this.filterForm.statuses.indexOf(item.status) != -1)
              && (this.filterForm.shapes.indexOf(item.shape) != -1);

      // if (item[key].toLowerCase().indexOf(self.filterForm[filterableProps[key]].toLowerCase()) == -1)

    });
    console.log(filteredData);
    this.filteredEnvironments = filteredData;
  }

  resetFilterConfigurations() : void {
    this.filterForm.resetConfigurations();
    this.buildGrid();
  }

  buildGrid() : void {
    this.userResourceService.getUserProvisionedResources()
      .subscribe((result) => {
        this.environments = this.loadEnvironments(result);

        this.filteredEnvironments = this.environments;

        this.getDefaultFilterConfiguration();
        console.log('models ', this.environments);
      });
  }

  containsNotebook(notebook_name: string) : boolean {
    if(notebook_name)
      for (var index = 0; index < this.environments.length; index++)
        if(notebook_name.toLowerCase() ==  this.environments[index].name.toString().toLowerCase())
          return true;

        return false;
  }

  loadEnvironments(exploratoryList: Array<any>) : Array<ResourcesGridRowModel> {
     if (exploratoryList) {
       return exploratoryList.map((value) => {
         return new ResourcesGridRowModel(value.exploratory_name,
           value.status,
           value.shape,
           value.computational_resources,
           value.up_time,
           value.exploratory_url,
           value.edge_node_ip);
       });
     }
   }

  printDetailEnvironmentModal(data) : void {
    this.detailDialog.open({ isFooter: false }, data);
  }

  exploratoryAction(data, action:string) {
    console.log('action ' + action, data);
    if (action === 'deploy') {
      this.notebookName = data.name;
      this.computationalResourceModal.open({ isFooter: false },  data);
    } else if (action === 'run') {
      this.userResourceService
        .runExploratoryEnvironment({notebook_instance_name: data.name})
        .subscribe((result) => {
          console.log('startUsernotebook result: ', result);
          this.buildGrid();
        });
    } else if (action === 'stop') {
      this.confirmationDialog.open({ isFooter: false }, data, ConfirmationDialogType.StopExploratory);
    } else if (action === 'terminate') {
      this.confirmationDialog.open({ isFooter: false }, data, ConfirmationDialogType.TerminateExploratory);
    }
  }

  dropdownPosition(event) : void{
    let contentHeight = document.body.offsetHeight > window.outerHeight ? document.body.offsetHeight : window.outerHeight;
    this.isOutscreenDropdown = event.pageY + 215 > contentHeight ? true : false;
  }
}
