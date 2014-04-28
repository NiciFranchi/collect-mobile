package org.openforis.collect.android.viewmodelmanager

import org.openforis.collect.android.viewmodel.*
import spock.lang.Specification

import static java.lang.String.valueOf
import static org.openforis.collect.android.IdGenerator.nextId
import static org.openforis.collect.android.viewmodel.UiNode.Status.*
import static org.openforis.collect.android.viewmodel.UiValidationError.Level.ERROR
import static org.openforis.collect.android.viewmodel.UiValidationError.Level.WARNING

/**
 * @author Daniel Wiell
 */
class ViewModelManagerTest extends Specification {
    def repo = Mock(ViewModelRepository)
    def attribute = new UiTextAttribute(1, new Definition(valueOf(nextId()), 'name', 'label'))
    def entity = new UiEntity(1, new Definition(valueOf(nextId()), 'name', 'label'))

    def manager = new ViewModelManager(repo)

    def setup() {
        def collection = new UiRecordCollection(nextId(), new Definition(valueOf(nextId()), 'name', 'label'))
        def record = new UiRecord(nextId(), new Definition(valueOf(nextId()), 'name', 'label'), collection)
        record.addChild(entity)
        def survey = new UiSurvey(nextId(), new Definition(valueOf(nextId()), 'name', 'label'))
        survey.addChild(collection)
        survey.addRecord(record)
        repo.surveyRecords(_) >> []
        entity.addChild(attribute)
    }

    def 'When updating an empty attribute with a value, status is change from EMPTY to OK'() {
        attribute.status = EMPTY
        attribute.text = 'Non-empty value'

        when:
        manager.updateAttribute(attribute, validationErrors(attribute))

        then:
        attribute.status == OK
        1 * repo.updateAttribute(attribute)
    }


    def 'When updating an attribute with validation warnings, status is changed to VALIDATION_WARNING'() {
        when:
        manager.updateAttribute(attribute, validationErrors(attribute, WARNING))

        then:
        attribute.status == VALIDATION_WARNING
        1 * repo.updateAttribute(attribute, _)
    }

    private def validationErrors(UiTextAttribute uiAttribute, UiValidationError.Level... levels) {
        def errors = levels.collect { new UiValidationError(it as String, it, uiAttribute) } as Set
        def validationErrorsByAttribute = [(uiAttribute): errors]
        validationErrorsByAttribute
    }


    def 'When updating an attribute with validation warnings and errors, status is changed to VALIDATION_ERROR'() {
        when:
        manager.updateAttribute(attribute, validationErrors(attribute, WARNING, ERROR))

        then:
        attribute.status == VALIDATION_ERROR
        1 * repo.updateAttribute(attribute, _)
    }


    def 'When updating an attribute and status changes, status of parent nodes are updated'() {
        assert entity.status == OK

        when:
        manager.updateAttribute(attribute, validationErrors(attribute, WARNING))
        then:
        entity.status == VALIDATION_WARNING
    }
}
