package wodss.timecastfrontend.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wodss.timecastfrontend.domain.Allocation;
import wodss.timecastfrontend.domain.Contract;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.TimecastPreconditionFailedException;
import wodss.timecastfrontend.services.AllocationService;
import wodss.timecastfrontend.services.ContractService;
import wodss.timecastfrontend.services.mocks.MockLoginService;

import javax.validation.Valid;
import java.util.List;

/**
 * Controlls the contract web component
 *
 */
@Controller
@RequestMapping(value = "/contracts")
public class ContractController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ContractService contractService;
    private final AllocationService allocationService;

    /**
     * Constructor
     * @param contractService
     * @param allocationService
     */
    @Autowired
    public ContractController(ContractService contractService, AllocationService allocationService) {
        this.contractService = contractService;
        this.allocationService = allocationService;
    }

    /**
     * Fetches all contracts to list on web page
     * @param model
     * @return
     */
    @GetMapping
    public String getAll(Model model) {
        logger.debug("Get all contracts");
        Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        List<Contract> contracts = contractService.getAll(token);
        model.addAttribute("contracts", contracts);

        return "contracts/list";
    }

    /**
     * Fetches specific contract to show on web page
     * @param id
     * @param model
     * @return next pagelink
     */
    @GetMapping("/{id}")
    public String getById(@PathVariable long id, Model model) {
        logger.debug("Get contract by id: " + id);
        Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Contract contract = contractService.getById(token, id);
        List<Contract> contracts = contractService.getByEmployee(token, contract.getEmployee());

        model.addAttribute("contract", contract);
        model.addAttribute("contracts", contracts);

        return "contracts/update";
    }

    /**
     * Updates contract
     * @param id
     * @param contract
     * @param bindingResult
     * @param model
     * @return next pagelink
     */
    @PutMapping("/{id}")
    public String updateById(@PathVariable Long id, @Valid @ModelAttribute("contract") Contract contract,
                             BindingResult bindingResult, Model model) {
        logger.debug("Update contract by id: " + id);
        Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        // Prevent update if there exist allocations with the same contract.
        List<Allocation> allocations = allocationService.getAllocations(token, contract.getEmployee().getId(), -1, null, null);
        if (allocations.size() > 0) {
            logger.debug("Cannot change Contract when there are allocations referencing to this contract.");
            model.addAttribute("exception", "Cannot change Contract when there are allocations referencing to this contract.");
            return "contracts/update";
        }

        if (bindingResult.hasErrors()) {
            logger.debug("Binding error: " + bindingResult.getAllErrors());
            return "contracts/update";
        }

        try {
            contract.setId(id);
            Contract updatedContract = contractService.update(token, contract);
            List<Contract> contracts = contractService.getByEmployee(token, updatedContract.getEmployee());
            model.addAttribute("contract", updatedContract);
            model.addAttribute("contracts", contracts);
            model.addAttribute("success", "Successfully updated Contract.");
            return "contracts/update";
        } catch (TimecastPreconditionFailedException ex) {
            model.addAttribute("exception", ex.getMessage());
            return "contracts/update";
        }
    }

    /**
     * Delete contract
     * @param id
     * @param redirectAttributes
     * @return redirect contract list
     */
    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.debug("Delete contract by id: " + id);
        Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        try {
            contractService.deleteById(token, id);
        } catch (TimecastPreconditionFailedException ex) {
            redirectAttributes.addFlashAttribute("exception", "Cannot delete Contract when there are still allocations referencing to this contract.");
            return "redirect:/contracts/" + id;
        }

        redirectAttributes.addFlashAttribute("success", "Successfully deleted Contract.");
        return "redirect:/contracts";
    }
}
