/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2020 Micromata GmbH, Germany (www.micromata.com)
//
// ProjectForge is dual-licensed.
//
// This community edition is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as published
// by the Free Software Foundation; version 3 of the License.
//
// This community edition is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, see http://www.gnu.org/licenses/.
//
/////////////////////////////////////////////////////////////////////////////

package org.projectforge.rest.fibu

import org.projectforge.business.fibu.KundeDao
import org.projectforge.business.fibu.ProjektDO
import org.projectforge.business.fibu.ProjektDao
import org.projectforge.business.fibu.kost.Kost2DO
import org.projectforge.business.fibu.kost.Kost2Dao
import org.projectforge.business.fibu.kost.KostCache
import org.projectforge.rest.config.Rest
import org.projectforge.rest.core.AbstractDTOPagesRest
import org.projectforge.rest.dto.Project
import org.projectforge.ui.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("${Rest.URL}/project")
class ProjectPagesRest
    : AbstractDTOPagesRest<ProjektDO, Project, ProjektDao>(
        ProjektDao::class.java,
        "fibu.projekt.title") {

    @Autowired
    private val kostCache: KostCache? = null

    @Autowired
    private val kundeDao: KundeDao? = null

    override fun transformFromDB(obj: ProjektDO, editMode: Boolean): Project {
        val projekt = Project()
        projekt.copyFrom(obj)
        projekt.transformKost2(kostCache?.getAllKost2Arts(obj.id))
        projekt.bereich = obj.bereich ?: 0
        return projekt
    }

    override fun transformForDB(dto: Project): ProjektDO {
        val projektDO = ProjektDO()
        dto.copyTo(projektDO)
        projektDO.internKost2_4 = dto.bereich
        if (dto.customer != null) {
            projektDO.kunde = kundeDao!!.getById(dto.customer!!.id)
        }
        return projektDO
    }

    override val classicsLinkListUrl: String?
        get() = "wa/projectList"

    /**
     * LAYOUT List page
     */
    override fun createListLayout(): UILayout {
        val layout = super.createListLayout()
                .add(UITable.createUIResultSetTable()
                        .add(UITableColumn("kost", title = "fibu.projekt.nummer"))
                        .add(lc, "identifier")
                        .add(UITableColumn("customer.name", title = "fibu.kunde.name"))
                        .add(lc, "name", "kunde.division", "task", "konto", "status", "projektManagerGroup")
                        .add(UITableColumn("kost2ArtsAsString", title = "fibu.kost2art.kost2arten"))
                        .add(lc, "description"))
        layout.getTableColumnById("konto").formatter = Formatter.KONTO
        layout.getTableColumnById("task").formatter = Formatter.TASK_PATH
        layout.getTableColumnById("projektManagerGroup").formatter = Formatter.GROUP
        return LayoutUtils.processListPage(layout, this)
    }

    /**
     * LAYOUT Edit page
     */
    override fun createEditLayout(dto: Project, userAccess: UILayout.UserAccess): UILayout {
        val costNumber = UICustomized("cost.number24")
                .add("nummer", dto.nummer)
                .add("bereich", dto.bereich)

        val cost2Selector = UICustomized("cost2.art.select")

        val konto = UIInput("konto", lc, tooltip = "fibu.projekt.konto.tooltip")

        val layout = super.createEditLayout(dto, userAccess)
                .add(costNumber)
                .add(UISelect.createCustomerSelect(lc, "customer", false, "fibu.kunde"))
                .add(konto)
                .add(lc, "name", "identifier", "task")
                .add(UISelect.createGroupSelect(lc, "projektManagerGroup", false, "fibu.projekt.projektManagerGroup"))
                .add(lc, "projectManager", "headOfBusinessManager", "description")

        layout.add(cost2Selector)

        return LayoutUtils.processEditPage(layout, dto, this)
    }

    override val autoCompleteSearchFields = arrayOf("name", "identifier")
}
