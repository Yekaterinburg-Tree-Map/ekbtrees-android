package ru.ekbtrees.treemap.ui.edittree.extentions

import androidx.core.widget.doOnTextChanged
import ru.ekbtrees.treemap.databinding.FragmentEditTreeBinding
import ru.ekbtrees.treemap.ui.common.extentions.addOnItemSelectedListener
import ru.ekbtrees.treemap.ui.common.extentions.addOnProgressChangeListener
import ru.ekbtrees.treemap.ui.mvi.contract.EditTreeInputField

fun FragmentEditTreeBinding.getInputListeners(onValueChange: (EditTreeInputField) -> Unit) {
    heightOfTheFirstBranchValue.doOnTextChanged { text, _, _, _ ->
        text?.let {
            onValueChange(EditTreeInputField.HeightOfTheFirstBranch(text.toString()))
        }
    }
    numberOfTrunksValue.doOnTextChanged { text, _, _, _ ->
        text?.let {
            onValueChange(EditTreeInputField.NumberOfTrunks(text.toString()))
        }
    }
    trunkGirthValue.doOnTextChanged { text, _, _, _ ->
        text?.let {
            onValueChange(EditTreeInputField.TrunkGirth(text.toString()))
        }
    }
    diameterOfCrownValue.doOnTextChanged { text, _, _, _ ->
        text?.let {
            onValueChange(EditTreeInputField.DiameterOfCrown(text.toString()))
        }
    }
    heightOfTheFirstBranchValue.doOnTextChanged { text, _, _, _ ->
        text?.let {
            onValueChange(EditTreeInputField.HeightOfTheFirstBranch(text.toString()))
        }
    }
    ageValue.doOnTextChanged { text, _, _, _ ->
        text?.let {
            onValueChange(EditTreeInputField.Age(text.toString()))
        }
    }
    conditionAssessmentValue.addOnProgressChangeListener { progress ->
        onValueChange(EditTreeInputField.ConditionAssessment(progress.toString()))
    }
    plantingTypeValue.addOnItemSelectedListener { itemText ->
        onValueChange(EditTreeInputField.PlantingType(itemText))
    }
    treeSpeciesValue.addOnItemSelectedListener { itemText ->
        onValueChange(EditTreeInputField.Species(itemText))
    }
    treeStatusValue.addOnItemSelectedListener { itemText ->
        onValueChange(EditTreeInputField.Status(itemText))
    }
}